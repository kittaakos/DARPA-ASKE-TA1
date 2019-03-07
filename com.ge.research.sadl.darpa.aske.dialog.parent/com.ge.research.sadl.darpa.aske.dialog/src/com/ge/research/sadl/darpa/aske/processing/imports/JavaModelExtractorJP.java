package com.ge.research.sadl.darpa.aske.processing.imports;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.ge.research.sadl.builder.ConfigurationManagerForIdeFactory;
import com.ge.research.sadl.builder.IConfigurationManagerForIDE;
import com.ge.research.sadl.darpa.aske.curation.AnswerCurationManager;
import com.ge.research.sadl.darpa.aske.processing.DialogConstants;
import com.ge.research.sadl.darpa.aske.processing.imports.SadlModelGenerator.SadlMethod;
import com.ge.research.sadl.jena.inference.SadlJenaModelGetterPutter;
import com.ge.research.sadl.reasoner.ConfigurationException;
import com.ge.research.sadl.reasoner.ConfigurationManager;
import com.ge.research.sadl.reasoner.IConfigurationManagerForEditing.Scope;
import com.github.javaparser.JavaParser;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class JavaModelExtractorJP implements IModelFromCodeExtractor {
    private static final Logger logger = Logger.getLogger (JavaModelExtractorJP.class) ;
	private SadlModelGenerator smg = null;
	private String packageName = "";
	private String type = null;
	private String typeComment = null;
	private List<SadlMethod> methods = new ArrayList<SadlMethod>();
	private Set<String> names = new HashSet<String>();
	private Map<String, String> classDeclarations = new HashMap<String, String>();
	private List<Comment> comments = new ArrayList<Comment>();
	private AnswerCurationManager curationMgr = null;
	
	public enum CONTEXT {PackageDecl, MainClassDecl, InnerClassDecl, ConstructorDecl, MethodDecl, Expression,
		Block, MethodBody}
	public enum USAGE {Defined, Used, Reassigned}
	public enum InputOutput {Input, Output}
	private CONTEXT currentContext = null;
	private Map<String, String> preferences;
	Map<String, Tag> tagMap = null;;
	private List<File> codeFiles;
	private OntModel codeModel;
	private String codeModelName;	// the name of the model  being created by extraction
	private String codeModelPrefix; // the prefix of the model being created by extraction
	
	private String codeMetaModelModelFolder = null;
	private String codeMetaModelUri;	// the name of the code extraction metamodel
	private String codeMetaModelPrefix;	// the prefix of the code extraction metamodel
	private IConfigurationManagerForIDE codeMetaModelConfigMgr;	// the ConfigurationManager used to access the code extraction metamodel
	private Individual rootContainingInstance = null;
	private boolean includeSerialization = true;
	private String defaultCodeModelName = null;
	private String defaultCodeModelPrefix = null;
	
	private static class MethodNameCollector extends VoidVisitorAdapter<List<MethodDeclaration>> {	
		@Override
		public void visit(MethodDeclaration md, List<MethodDeclaration> collector) {
			super.visit(md,  collector);
			collector.add(md);
		}
	}
	
	public JavaModelExtractorJP(AnswerCurationManager acm, SadlModelGenerator gen, Map<String, String> preferences) {
		setCurationMgr(acm);
		smg = gen;
		this.setPreferences(preferences);
	    logger.setLevel(Level.ALL);
	}
	
	private void initializeContent() {
		packageName = "";
		type = null;
		
		methods.clear();
		names.clear();
		getComments().clear();
	}

	public boolean process(String content, boolean includeSerialization) throws ConfigurationException, IOException {
		setIncludeSerialization(includeSerialization);
		parse(getCurationMgr().getOwlModelsFolder(), content);
		// Create a Reasoner and reason over the model, getting resulting InfModel
		// get deductions from InfModel, add to code model.
//		getCodeModel().write(System.out, "RDF/XML-ABBREV");
		return true;
	}
	
	private void setIncludeSerialization(boolean includeSerialization) {
		this.includeSerialization = includeSerialization;
	}

	private boolean isIncludeSerialization() {
		return includeSerialization;
	}

	//use ASTParse to parse string
	private void parse(String modelFolder, String javaCodeContent) throws IOException, ConfigurationException {
		try {
			notifyUser(modelFolder, javaCodeContent);
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    initializeContent();
		
		logger.debug("***************** code to process ******************");
//		logger.debug(str);
		logger.debug("****************************************************");
		
		TextProcessor txtpr = new TextProcessor(getPreferences());
//		try {
//			String result = txtpr.process(null, null);
//			logger.debug("test of text processor service:\n" + result);
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
        // Set up a minimal type solver that only looks at the classes used to run this sample.
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());

        // Configure JavaParser to use type resolution
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        JavaParser.getStaticConfiguration().setSymbolResolver(symbolSolver);

        // Parse some code
        CompilationUnit cu = JavaParser.parse(javaCodeContent);
        Optional<PackageDeclaration> pkg = cu.getPackageDeclaration();
        if (pkg.isPresent()) {
        	setPackageName(pkg.get().getNameAsString());
        	String rootClassName = getRootClassName(cu);
        	setCodeModelName(getPackageName() + "/" + rootClassName);
        	setCodeModelPrefix(derivePrefixFromPackage(getCodeModelName()));
        }
        else {
    		setCodeModelName(getDefaultCodeModelName());
    		setCodeModelPrefix(getDefaultCodeModelPrefix());

        }
        initializeCodeModel(getCodeMetaModelModelFolder());
        processBlock(cu, null);
	}

	private String getRootClassName(Node nd) {
		if (nd instanceof ClassOrInterfaceDeclaration) {
			return ((ClassOrInterfaceDeclaration)nd).getNameAsString();
		}
		List<ClassOrInterfaceDeclaration> clses = nd.findAll(ClassOrInterfaceDeclaration.class);
		if (clses != null && clses.size() > 0) {
			return clses.get(0).getNameAsString();
		}
		return "NoNameFound";
	}

	private String derivePrefixFromPackage(String pkg) {
		if (pkg.contains(".")) {
			StringBuilder sb = new StringBuilder();
			int lastStart = 0;
			int dotLoc = pkg.indexOf('.');
			while (dotLoc > 0) {
				sb.append(pkg.substring(lastStart, lastStart + 1));
				lastStart = dotLoc + 1;
				dotLoc = pkg.indexOf('.', dotLoc + 1);
			}
			return sb.toString();
		}
		else {
			return pkg;
		}
	}

	private void initializeCodeModel(String extractionMetaModelModelFolder) throws ConfigurationException, IOException {
		if (getCurationMgr().getExtractionProcessor().getCodeModel() == null) {
			// create new code model
			
			setCodeMetaModelConfigMgr(ConfigurationManagerForIdeFactory.getConfigurationManagerForIDE(extractionMetaModelModelFolder, null)); //getCurationMgr().getProjectConfigurationManager());
			OntDocumentManager owlDocMgr = getCodeMetaModelConfigMgr().getJenaDocumentMgr();
			OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
			if (extractionMetaModelModelFolder != null) { // && !modelFolderPathname.startsWith(SYNTHETIC_FROM_TEST)) {
				File mff = new File(extractionMetaModelModelFolder);
				mff.mkdirs();
				spec.setImportModelGetter(new SadlJenaModelGetterPutter(spec, extractionMetaModelModelFolder));
			}
			if (owlDocMgr != null) {
				spec.setDocumentManager(owlDocMgr);
				owlDocMgr.setProcessImports(true);
			}
			getCurationMgr().getExtractionProcessor().setCodeModel(ModelFactory.createOntologyModel(spec));	
			setCodeModel(getCurationMgr().getExtractionProcessor().getCodeModel());
			getCodeModel().setNsPrefix(getCodeModelPrefix(), getCodeModelNamespace());
			Ontology modelOntology = getCodeModel().createOntology(getCodeModelName());
			logger.debug("Ontology '" + getCodeModelName() + "' created");
			modelOntology.addComment("This ontology was created by extraction from code by the ANSWER JavaModelExtractorJP.", "en");
			setCodeMetaModelUri("http://sadl.org/CodeModel.sadl");
			setCodeMetaModelPrefix("codemdl");
			OntModel importedOntModel = getCodeMetaModelConfigMgr().getOntModel(getCodeMetaModelUri(), Scope.INCLUDEIMPORTS);
			addImportToJenaModel(getCodeModelName(), getCodeMetaModelUri(), getCodeMetaModelPrefix(), importedOntModel);
		}
		else {
			setCodeModel(getCurationMgr().getExtractionProcessor().getCodeModel());
		}
		
	}

	private void processBlock(Node blkNode, Individual containingInst) {
		List<Node> childNodes = blkNode.getChildNodes();
		for (int i = 0; i < childNodes.size(); i++) {
			Node childNode = childNodes.get(i);
			processBlockChild(childNode, containingInst, null);
		}
		investigateComments(blkNode, containingInst);
	}

	private void processBlockChild(Node childNode, Individual containingInst, USAGE knownUsage) {
		if (childNode instanceof ClassOrInterfaceDeclaration) {
			ClassOrInterfaceDeclaration cls = (ClassOrInterfaceDeclaration) childNode;
			Individual blkInst = getOrCreateClass(cls);
			if (containingInst != null) {
				getCodeModel().add(blkInst, getContainedInProperty(), containingInst);
			}
			else {
				setRootContainingInstance(blkInst);
			}
			processBlock(cls, blkInst);
			addSerialization(blkInst, cls.toString());
		}
		else if (childNode instanceof FieldDeclaration) {
			FieldDeclaration fd = (FieldDeclaration) childNode;
			NodeList<VariableDeclarator> vars = fd.getVariables();
			vars.forEach(var -> {
				processBlockChild(var, containingInst, USAGE.Defined);
			});
		}
		else if (childNode instanceof MethodDeclaration) {
			MethodDeclaration m = (MethodDeclaration) childNode;
			Comment cmnt = getComment(m);
			Individual methInst = getOrCreateMethod(m, containingInst);
			if (containingInst != null) {
				getCodeModel().add(methInst, getContainedInProperty(), containingInst);
			}
			// Note that this local scope overrides any variable of the same name in a
			//	more global scope. Therefore do not look for an existing variable
			NodeList<Parameter> args = m.getParameters();
			List<Individual> argList = args.size() > 0 ? new ArrayList<Individual>() : null;
			for (int j = 0; j < args.size(); j++) {
				Parameter param = args.get(j);
				String nm = param.getNameAsString();
				try {
					Individual argCV = getOrCreateCodeVariable(param, methInst, getMethodVariableClass());
					argList.add(argCV);
				} catch (CodeExtractionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (argList != null) {
				RDFList argInstList = getCodeModel().createList(argList.iterator());
				methInst.addProperty(getArgumentsProperty(), argInstList);
			}
			processBlock(m, methInst);	// order matters--do this after parameters and before return
			String rt = m.getTypeAsString();
			if (rt != null) {
				List<Literal> rtypes = new ArrayList<Literal>();
				rtypes.add(getCodeModel().createTypedLiteral(rt));
				RDFList rtList = getCodeModel().createList(rtypes.iterator());
				methInst.addProperty(getReturnTypeProperty(), rtList);
			}
			System.out.println(methInst.getURI() + " returns " + ((rt != null && rt.length() > 0) ? rt : "void"));
			addSerialization(methInst, ((MethodDeclaration) childNode).toString());
		}
		else if (childNode instanceof MethodCallExpr) {
			MethodCallExpr mc = (MethodCallExpr)childNode;
        	NodeList<Expression> args = mc.getArguments();
        	Iterator<Expression> nlitr = args.iterator();
        	while (nlitr.hasNext()) {
        		Expression expr = nlitr.next();
//        		System.out.println("Argument Expression: " + expr.toString() + " (type " + expr.getClass().getCanonicalName() + ")");
        		if (expr instanceof NameExpr) {
        			try {
						setSetterArgument(mc, (NameExpr)expr, containingInst);
					} catch (CodeExtractionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        		else {
           			processBlockChild(expr, containingInst, USAGE.Defined);        			
        		}
        	}
		}
		else if (childNode instanceof BlockStmt) {
			processBlock(childNode, containingInst);
		}
		else if (childNode instanceof ExpressionStmt) {
			ExpressionStmt es = (ExpressionStmt)childNode;
			Expression expr = es.getExpression();
			if (expr instanceof AssignExpr) {
				processBlockChild(expr, containingInst, knownUsage);
			}
			else if (expr instanceof VariableDeclarationExpr) {
				processBlockChild(expr, containingInst, USAGE.Defined);
			}
			else {
				processBlock(expr, containingInst);
			}
		}
		else if (childNode instanceof EnclosedExpr) {
			processBlock(childNode, containingInst);
		}
		else if (childNode instanceof VariableDeclarationExpr) {
			VariableDeclarationExpr vdecl = (VariableDeclarationExpr)childNode; 
			try {
				OntClass codeVarClass = getMethodVariableClass();
				Individual vdInst = getOrCreateCodeVariable(vdecl, containingInst, codeVarClass);
			} catch (CodeExtractionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else if (childNode instanceof VariableDeclarator) {
			try {
				OntClass codeVarClass = getClassFieldClass();	// TODO is this always field or only in a class?
				Individual fdInst = getOrCreateCodeVariable(childNode, containingInst, codeVarClass);
			} catch (CodeExtractionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			Comment cmnt = getComment(childNode);
//			System.out.println("   Variable Declaration: " + ((VariableDeclarator) childNode).getTypeAsString() + " " + ((VariableDeclarator) childNode).getNameAsString() + (cmnt != null ? "(Comment: " + cmnt.toString().trim() + ")" : ""));    
		}
		else if (childNode instanceof AssignExpr) {
			AssignExpr ass = (AssignExpr)childNode;
			List<Node> assChildren = ass.getChildNodes();
			for (int j = 0; j < assChildren.size(); j++) {
				processBlockChild(assChildren.get(j), containingInst, (j == 0 ? USAGE.Reassigned : USAGE.Used));					
			}
		}
		else if (childNode instanceof BinaryExpr) {
			processBlock(childNode, containingInst);
		}
		else if (childNode instanceof NameExpr) {
			String nm = ((NameExpr)childNode).getNameAsString();
			findCodeVariableAndAddReference(childNode, nm, containingInst, knownUsage, true, null);
		}
		else if (childNode instanceof IfStmt) {
			Expression cond = ((IfStmt)childNode).getCondition();
			processBlockChild(cond, containingInst, USAGE.Used);
			List<Node> condChildren = ((IfStmt)childNode).getChildNodes();
			for (int j = 1; j < condChildren.size(); j++) {
				processBlockChild(condChildren.get(j), containingInst, null);
			}
		}
		else if (childNode instanceof ReturnStmt) {
			List<Node> returnChildren = ((ReturnStmt)childNode).getChildNodes();
			Node ret0 = returnChildren.get(0);
			if (ret0 instanceof NameExpr) {
				// this is an output of this block. It should already exist.
				String nm = ((NameExpr)ret0).getNameAsString();
				findCodeVariableAndAddReference(ret0, nm, containingInst, USAGE.Used, true, InputOutput.Output);
//				Individual ref = findCodeVariableAndAddReference(ret0, nm, containingInst, USAGE.Used);
//				ref.addProperty(getOutputProperty(), getCodeModel().createTypedLiteral(true));
			}
			else {
				for (int j = 0; j < returnChildren.size(); j++) {
					processBlockChild(returnChildren.get(j), containingInst, null);
				}
			}
		}
		else if (childNode instanceof BlockComment) {
			String cntnt = ((BlockComment)childNode).getContent();
			if (cntnt != null) {
				investigateComment(childNode, containingInst, (BlockComment)childNode);
			}
			List<Comment> cmnts = ((BlockComment)childNode).getAllContainedComments();
			for (int i = 0; cmnts != null && i < cmnts.size(); i++) {
				Comment cmt = cmnts.get(i);
				investigateComment(childNode, containingInst, cmt);
			}
		}
//		else if (childNode instanceof ImportDeclaration) {
//			System.out.println("Ignoring '" + childNode.toString() + "'");
//		}
		else {
			System.err.println("Block child unhandled Node '" + childNode.toString().trim() + "' of type " + childNode.getClass().getCanonicalName());
		}
		investigateComments(childNode, containingInst);
	}

	private void addSerialization(Individual blkInst, String code) {
		if (isIncludeSerialization()) {
			blkInst.addProperty(getSerializationProperty(), getCodeModel().createTypedLiteral(code));
		}
	}

	private boolean setRootContainingInstance(Individual thisInst) {
		if (rootContainingInstance == null) {
			rootContainingInstance = thisInst;
			return true;
		}
		return false;
	}

	private void investigateComments(Node childNode, Individual subject) {
		Comment cmt = getComment(childNode);
		investigateComment(childNode, subject, cmt);
		List<Comment> ocmts = childNode.getOrphanComments();
		for (int i = 0; ocmts != null && i < ocmts.size(); i++) {
			cmt = ocmts.get(i);
			Optional<Range> rng = cmt.getRange();
			if (rng.isPresent()) {
				System.out.println("Found orphaned comment at line " + rng.get().getLineCount() + "(" + rng.get().begin.toString() + " to " + rng.get().end.toString() + ")");
			}
			else {
				System.out.println("Found orphaned comment but range not known");
			}
			System.out.println("   " + cmt.getContent());
		}
	}

	private void investigateComment(Node childNode, Individual subject, Comment cmt) {
		if (cmt != null) {
			Optional<Range> rng = childNode.getRange();
			if (rng.isPresent()) {
				System.out.println("Found comment at line " + rng.get().getLineCount() + "(" + rng.get().begin.toString() + " to " + rng.get().end.toString() + ")");
			}
			else {
				System.out.println("Found comment but range not known");
			}
			System.out.println("   " + cmt.getContent());
			Individual cmtInst = getCodeModel().createIndividual(getCommentClass());
			if (subject == null) {
				subject = rootContainingInstance;
			}
			if (subject != null) {
				subject.addProperty(getCommentProperty(), cmtInst);
				cmtInst.addProperty(getCommentContentProperty(), getCodeModel().createTypedLiteral(cmt.getContent()));
				if (rng.isPresent()) {
					cmtInst.addProperty(getBeginsAtProperty(), getCodeModel().createTypedLiteral(rng.get().begin.line));
					cmtInst.addProperty(getEndsAtProperty(), getCodeModel().createTypedLiteral(rng.get().end.line));
				}
			}
			else {
				System.err.println("Unable to add comment because there is no known subject");
			}
		}
	}

	private Property getInputProperty() {
		return getCodeModel().getOntProperty(getCodeMetaModelUri() + "#input");
	}

	private Property getOutputProperty() {
		return getCodeModel().getOntProperty(getCodeMetaModelUri() + "#output");
	}

	private Individual findCodeVariableAndAddReference(Node childNode, String nm, Individual containingInst, USAGE usage, boolean lookToLargerScope, InputOutput inputOutput) {
//		String nnm = containingInst.getLocalName() + "." + nm;
		String nnm = constructCodeVariableUri(childNode, nm);

		Individual varInst = lookToLargerScope ? findDefinedVariable(nm, containingInst) : null;
		if (varInst == null && !lookToLargerScope) {
			try {
				varInst = getOrCreateCodeVariable(childNode, nm, nnm, containingInst, getCodeVariableClass(childNode), inputOutput);
			} catch (CodeExtractionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (varInst != null) {
			try {
				Individual ref = createReference(childNode, varInst, containingInst, usage != null ? usage : USAGE.Used);
	          	setInputOutputIfKnown(ref, inputOutput);
			} catch (CodeExtractionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			System.err.println("NameExpr (" + nnm + ") not found; it should already exist!");
		}
		return varInst;
	}

	private OntClass getCodeVariableClass(Node varNode) {
		if (varNode instanceof Parameter) {
			return getMethodArgumentClass();
		}
		else if (varNode instanceof FieldDeclaration) {
			return getClassFieldClass();
		}
		else {
			Optional<Node> parent = varNode.getParentNode();
			while (parent.isPresent()) {
				if (parent.isPresent()) {
					if (parent.get() instanceof MethodDeclaration) {
						return getMethodVariableClass();
					}
					else if (parent.get() instanceof ClassOrInterfaceDeclaration) {
						return getClassFieldClass();
					}
				}
				parent = parent.get().getParentNode();
			}
		}
		return null;
	}

	private Individual findDefinedVariable(String nm, Individual containingInst) {
		Individual inst = containingInst;
		String nnm = getCodeModelNamespace() + inst.getLocalName() + "." + nm;
		Individual varInst = getCodeModel().getIndividual(nnm);
		if (varInst == null) {
			RDFNode obj = containingInst.getPropertyValue(getContainedInProperty());
			if (obj != null && obj.isURIResource() && obj.canAs(Individual.class)) {
				return findDefinedVariable(nm, obj.as(Individual.class));
			}
		}
		return varInst;
	}

	private void setSetterArgument(MethodCallExpr mc, NameExpr arg, Individual containingInst) throws CodeExtractionException {
		boolean isArgToSetter = false;
		if (mc.getNameAsString().startsWith("set") || mc.getNameAsString().contains(".set")) {
			isArgToSetter = true;
		}
		else {
			Optional<Node> parent = mc.getParentNode();
			while (parent.isPresent()) {
				if (parent.get() instanceof MethodCallExpr) {
					if (((MethodCallExpr)parent.get()).getNameAsString().startsWith("set") ||
							((MethodCallExpr)parent.get()).getNameAsString().startsWith(".set")) {
						isArgToSetter = true;
						break;
					}
				}
				parent = parent.get().getParentNode();
			}	
		}
		if (isArgToSetter) {
			Individual cvInst = findCodeVariableAndAddReference(mc, arg.getNameAsString(), containingInst, USAGE.Used, true, InputOutput.Output);
//			OntClass codeVarClass = getMethodVariableClass();
//			Individual cvInst = getOrCreateCodeVariable(arg, containingInst, codeVarClass);
			cvInst.setPropertyValue(getSetterArgumentProperty(), getCodeModel().createTypedLiteral(true));
		}
	}

	private Individual createReference(Node varNode, Individual codeVarInst, Individual containingInst, USAGE usage) throws CodeExtractionException {
		int beginsAt = varNode.getRange().get().begin.line;
		int endsAt = varNode.getRange().get().end.line;
//		NodeWithSimpleName<?> codeBlockNode = getContainingCodeBlockNode(varNode);
//		Individual blkInst = null;
//		if (codeBlockNode != null) {
//			blkInst = getCodeModel().getIndividual(getCodeModelNamespace() + codeBlockNode.getNameAsString());
//		}
		Individual ref = createNewReference(containingInst, beginsAt, endsAt, usage);
		codeVarInst.addProperty(getReferenceProperty(), ref);
		return ref;
	}

	private Individual createNewReference(Individual blkInst, int beginsAt, int endsAt, USAGE usage) throws CodeExtractionException {
		Individual refInst = getCodeModel().createIndividual(getReferenceClass());
		if (blkInst != null) {
			refInst.addProperty(getCodeBlockProperty(), blkInst);
		}
		if (beginsAt >= 0) {
			refInst.addProperty(getBeginsAtProperty(), getCodeModel().createTypedLiteral(beginsAt));
		}
		if (endsAt >= 0) {
			refInst.addProperty(getEndsAtProperty(), getCodeModel().createTypedLiteral(endsAt));
		}
		refInst.addProperty(getUsageProperty(), getUsageInstance(usage));
		return refInst;
	}

	private NodeWithSimpleName<?> getContainingCodeBlockNode(Node node) {
		NodeWithSimpleName<?> containingNode = null;
		Optional<Node> parent = node.getParentNode();
		while (parent.isPresent()) {
			Node n = parent.get();
			if (n instanceof ClassOrInterfaceDeclaration) {
				containingNode = (ClassOrInterfaceDeclaration)n;
				break;
			}
			else if (n instanceof MethodDeclaration) {
				containingNode = (MethodDeclaration)n;
				break;
			}
			parent = parent.get().getParentNode();
		}
		return containingNode;
	}

	private void indentifyAndLinkContainingBlock(BodyDeclaration<?> blkNode, Individual blkInst) {
		Optional<Node> pn = blkNode.getParentNode();
		if (pn.isPresent()) {
			Node n = pn.get();
			Individual containingInst = getOrCreateBlockInstance(n);
			if (containingInst != null) {
				getCodeModel().add(blkInst, getContainedInProperty(), containingInst);
			}
		}
	}

	private Individual getOrCreateMethod(MethodDeclaration m, Individual containingInst) {
		Individual methInst = null;
    	String nm = m.getNameAsString();
//    	String nnm = containingInst.getLocalName() + "." + nm;
		String nnm = constructCodeVariableUri(m, nm);
    	methInst = getCodeModel().getIndividual(getCodeModelNamespace() + nnm);
    	if (methInst == null) {
    		methInst = getCodeModel().createIndividual(getCodeModelNamespace() + nnm, getCodeBlockMethodClass());
    	}
		return methInst;
	}

	private Individual getOrCreateCodeVariable(Node varNode, Individual containingInst, OntClass codeVarClass) throws CodeExtractionException {
		Individual cvInst = null;
		String origName = null;
		String nnm = null;
		if (varNode instanceof VariableDeclarator) {
			origName = ((VariableDeclarator)varNode).getNameAsString();
			if (containingInst != null) {
				nnm = constructCodeVariableUri(varNode, origName);
//				nnm = containingInst.getLocalName() + "." + origName;
			}
			else {
				nnm = origName;
			}
//			nnm = constructCodeVariableUri(varNode, origName);
			codeVarClass = getClassFieldClass();
		}
		else if (varNode instanceof NameExpr) {
			origName = ((NameExpr)varNode).getNameAsString();
			if (containingInst != null) {
				nnm = constructCodeVariableUri(varNode, origName);
//				nnm = containingInst.getLocalName() + "." + origName;
			}
			else {
				nnm = origName;
			}
//			nnm = constructCodeVariableUri(varNode, origName);
		}
		else if (varNode instanceof VariableDeclarationExpr) {
			NodeList<VariableDeclarator> vars = ((VariableDeclarationExpr)varNode).getVariables();
			for (int i = 0; i < vars.size(); i++) {
				if (i > 0) {
					System.err.println("Multiple vars (" + varNode.toString() + ") in VariableDeclarationExpr not current handled");
				}
				VariableDeclarator var = vars.get(i);
				origName = var.getNameAsString();
				if (containingInst != null) {
//					nnm = containingInst.getLocalName() + "." + origName;
					nnm = constructCodeVariableUri(varNode, origName);
				}
				else {
					nnm = origName;
				}
//				nnm = constructCodeVariableUri(vars.get(i), origName);
				codeVarClass = getMethodVariableClass();
				cvInst = getOrCreateCodeVariable(var, origName, nnm, containingInst, codeVarClass, null);
				if (vars.get(i).getInitializer().isPresent()) {
		          	createReference(var, cvInst, containingInst, USAGE.Reassigned);
				}
			}
		}
		else if (varNode instanceof Parameter) {
			NameExpr nm = ((Parameter)varNode).getNameAsExpression();
//			codeVarClass = getMethodVariableClass();
//			return getOrCreateCodeVariable(nm, containingInst, codeVarClass);
			return findCodeVariableAndAddReference(varNode, nm.getNameAsString(), containingInst, USAGE.Defined, false, InputOutput.Input);
		}
		else {
			throw new CodeExtractionException("Unexpected CodeVariable varNode type: " + varNode.getClass().getCanonicalName());
		}
		cvInst = getOrCreateCodeVariable(varNode, origName, nnm, containingInst, codeVarClass, null);
		return cvInst;
	}

	private Individual getOrCreateCodeVariable(Node varNode, String origName, String nnm, Individual containingInst,
			OntClass codeVarClass, InputOutput inputOutput) throws CodeExtractionException {
		Individual cvInst;
		cvInst = getCodeModel().getIndividual(getCodeModelNamespace() + nnm);
		if (cvInst == null && codeVarClass != null) {
			cvInst = getCodeModel().createIndividual(getCodeModelNamespace() + nnm, codeVarClass);
          	Individual ref = createReference(varNode, cvInst, containingInst, USAGE.Defined);
          	setInputOutputIfKnown(ref, inputOutput);
          	cvInst.addProperty(getVarNameProperty(), getCodeModel().createTypedLiteral(origName));
          	// TODO add varType
		}
		return cvInst;
	}

	private void setInputOutputIfKnown(Individual ref, InputOutput inputOutput) {
		if (inputOutput != null) {
			if (inputOutput.equals(InputOutput.Input)) {
				ref.addProperty(getInputProperty(), getCodeModel().createTypedLiteral(true));
			}
			else if (inputOutput.equals(InputOutput.Output)) {
				ref.addProperty(getOutputProperty(), getCodeModel().createTypedLiteral(true));
			}
		}
	}

	private String constructCodeVariableUri(Node varNode, String nm) {
		StringBuilder sb = new StringBuilder();
		//return ((VariableDeclarator)varNode).getNameAsString();
		Optional<Node> parent = varNode.getParentNode();
		while (parent.isPresent()) {
			if (parent.get() instanceof MethodDeclaration) {
				sb.insert(0,((MethodDeclaration)parent.get()).getNameAsString() + ".");
			}
//			else if (parent.get() instanceof FieldDeclaration) {
//				
//			}
			else if (parent.get() instanceof ClassOrInterfaceDeclaration) {
				sb.insert(0, ((ClassOrInterfaceDeclaration)parent.get()).getNameAsString() + ".");
			}
//			else {
//				System.out.println("Variable " + nm + " parent type: " + parent.get().getClass().getCanonicalName());
//			}
			parent = parent.get().getParentNode();
			if (parent.get() instanceof CompilationUnit) {
				break;
			}
		}
		sb.append(nm);
		return sb.toString();
	}

	private Individual getOrCreateClass(ClassOrInterfaceDeclaration cls) {
		Individual clsInst = null;
    	String nm = cls.getNameAsString();
    	clsInst = getCodeModel().getIndividual(getCodeModelNamespace() + nm);
    	if (clsInst == null) {
    		clsInst = getCodeModel().createIndividual(getCodeModelNamespace() + nm, getCodeBlockClassClass());
    	}
    	return clsInst;
	}

	private Individual getOrCreateBlockInstance(Node n) {
		Individual containingInst = null;
		if (n instanceof ClassOrInterfaceDeclaration) {
			containingInst = getCodeModel().getIndividual(getCodeModelNamespace() + ((ClassOrInterfaceDeclaration)n).getNameAsString());
			if (containingInst == null) {
				containingInst = getCodeModel().createIndividual(getCodeModelNamespace() + ((ClassOrInterfaceDeclaration)n).getNameAsString(), getCodeBlockClassClass());	
			}
		}
		else if (n instanceof MethodDeclaration) {
			containingInst = getCodeModel().getIndividual(getCodeModelNamespace() + ((MethodDeclaration)n).getNameAsString());
			if (containingInst == null) {
				containingInst = getCodeModel().createIndividual(getCodeModelNamespace() + ((MethodDeclaration)n).getNameAsString(), getCodeBlockClassClass());	
			}
		}
		return containingInst;
	}

	private Individual getUsageInstance(USAGE usage) throws CodeExtractionException {
		if (usage.equals(USAGE.Defined)) {
			return getCodeModel().getIndividual(getCodeMetaModelUri() + "#Defined");
		}
		else if (usage.equals(USAGE.Used)) {
			return getCodeModel().getIndividual(getCodeMetaModelUri() + "#Used");
		}
		else if (usage.equals(USAGE.Reassigned)) {
			return getCodeModel().getIndividual(getCodeMetaModelUri() + "#Reassigned");
		}
		throw new CodeExtractionException("Unexpected USAGE: " + usage.toString());
	}

	private Property getSetterArgumentProperty() {
		return getCodeModel().getOntProperty(getCodeMetaModelUri() + "#setterArgument");
	}

	private Property getUsageProperty() {
		return getCodeModel().getOntProperty(getCodeMetaModelUri() + "#usage");
	}

	private Property getCommentProperty() {
		return getCodeModel().getOntProperty(getCodeMetaModelUri() + "#comment");
	}

	private Property getCommentContentProperty() {
		return getCodeModel().getOntProperty(getCodeMetaModelUri() + "#commentContent");
	}

	private Property getSerializationProperty() {
		return getCodeModel().getOntProperty(getCodeMetaModelUri() + "#serialization");
	}
	
	private Property getArgumentsProperty() {
		return getCodeModel().getOntProperty(getCodeMetaModelUri() + "#arguments");
	}

	private Property getBeginsAtProperty() {
		return getCodeModel().getOntProperty(getCodeMetaModelUri() + "#beginsAt");
	}

	private Property getEndsAtProperty() {
		return getCodeModel().getOntProperty(getCodeMetaModelUri() + "#endsAt");
	}

	private Property getVarNameProperty() {
		return getCodeModel().getOntProperty(getCodeMetaModelUri() + "#varName");
	}

	private Property getVarTypeProperty() {
		return getCodeModel().getOntProperty(getCodeMetaModelUri() + "#varType");
	}

	private Property getCodeBlockProperty() {
		return getCodeModel().getOntProperty(getCodeMetaModelUri() + "#codeBlock");
	}

	private Property getReferenceProperty() {
		return getCodeModel().getOntProperty(getCodeMetaModelUri() + "#reference");
	}

	private Property getContainedInProperty() {
		return getCodeModel().getOntProperty(getCodeMetaModelUri() + "#containedIn");
	}

	private Property getReturnTypeProperty() {
		return getCodeModel().getOntProperty(getCodeMetaModelUri() + "#returnTypes");
	}

	private com.hp.hpl.jena.rdf.model.Resource getReferenceClass() {
		return getCodeModel().getOntClass(getCodeMetaModelUri() + "#Reference");
	}

//	private com.hp.hpl.jena.rdf.model.Resource getCodeVariableClass() {
//		return getCodeModel().getOntClass(getCodeMetaModelUri() + "#CodeVariable");
//	}

	private com.hp.hpl.jena.rdf.model.Resource getCodeBlockMethodClass() {
		return getCodeModel().getOntClass(getCodeMetaModelUri() + "#Method");
	}

	private com.hp.hpl.jena.rdf.model.Resource getCodeBlockClassClass() {
		return getCodeModel().getOntClass(getCodeMetaModelUri() + "#Class");
	}

	private OntClass getMethodArgumentClass() {
		return getCodeModel().getOntClass(getCodeMetaModelUri() + "#MethodArgument");
	}

	private OntClass getCommentClass() {
		return getCodeModel().getOntClass(getCodeMetaModelUri() + "#Comment");
	}

	private OntClass getClassFieldClass() {
		return getCodeModel().getOntClass(getCodeMetaModelUri() + "#ClassField");
	}

	private OntClass getMethodVariableClass() {
		return getCodeModel().getOntClass(getCodeMetaModelUri() + "#MethodVariable");
	}

	private void notifyUser(String modelFolder, String str) throws ConfigurationException {
		final String format = ConfigurationManager.RDF_XML_ABBREV_FORMAT;
		IConfigurationManagerForIDE configMgr = ConfigurationManagerForIdeFactory.getConfigurationManagerForIDE(modelFolder, format);
		Object dap = configMgr.getPrivateKeyValuePair(DialogConstants.DIALOG_ANSWER_PROVIDER);
		if (dap != null) {
			// talk to the user via the Dialog editor
			Method acmic = null;
			try {
				acmic = dap.getClass().getMethod("addCurationManagerInitiatedContent", String.class);
			} catch (NoSuchMethodException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (acmic == null) {
				Method[] dapMethods = dap.getClass().getDeclaredMethods();
				if (dapMethods != null) {
					for (Method m : dapMethods) {
						if (m.getName().equals("addCurationManagerInitiatedContent")) {
							acmic = m;
							break;
						}
					}
				}
			}
			if (acmic != null) {
				acmic.setAccessible(true);
				try {
					acmic.invoke(dap, str);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private Comment getComment(Node node) {
		if (node != null) {
			Optional<Comment> cmnt = node.getComment();
			if (cmnt.isPresent()) {
				return node.getComment().get();
			}
		}
		return null;
	}

	private List<NameExpr> findNamesIn(NodeList<Expression> callArgs) {
		List<NameExpr> names = new ArrayList<NameExpr>();
		callArgs.forEach(arg -> {
			if (arg instanceof NameExpr) {
				names.add((NameExpr) arg);
			}
			else if (arg instanceof BinaryExpr) {
				findNamesIn((BinaryExpr)arg, names);
			}
		});
		return names.size() > 0 ? names : null;
	}

	private List<NameExpr> findNamesIn(BinaryExpr be, List<NameExpr> names) {
		if (be.getLeft() instanceof NameExpr) {
			if (names == null) names = new ArrayList<NameExpr>();
			names.add((NameExpr) be.getLeft());
		}
		else if (be.getLeft() instanceof BinaryExpr) {
			names = findNamesIn((BinaryExpr)be.getLeft(), names);
		}
		if (be.getRight() instanceof NameExpr) {
			if (names == null) names = new ArrayList<NameExpr>();
			names.add((NameExpr) be.getRight());
		}
		else if (be.getRight() instanceof BinaryExpr) {
			names = findNamesIn((BinaryExpr)be.getRight(), names);
		}
		return names;
	}

	public String getPackageName() {
		return packageName;
	}

	private void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public Set<String> getNames() {
		return names;
	}

	void addName(String name) {
		if (!this.names.contains(name)) {
			this.names.add(name);
		}
	}
	
	public Map<String, String> getClassDeclarations() {
		return classDeclarations;
	}

	public void setClassDeclarations(Map<String, String> classDeclarations) {
		this.classDeclarations = classDeclarations;
	}
	
	public List<SadlMethod> getMethods() {
		return methods;
	}

	private void addMethod(SadlMethod mdecl) {
		this.methods.add(mdecl);
	}

	public List<Comment> getComments() {
		return comments;
	}

	private void addComment(Comment comment) {
		this.comments.add(comment);
	}

	@Override
	public String getType() {
		return type;
	}

	private void setType(String type) {
		this.type = type;
	}

	@Override
	public String getTypeComment() {
		return typeComment;
	}

	private void setTypeComment(String typeComment) {
		this.typeComment = typeComment;
	}

	@Override
	public Map<String, Tag> getTagMap() {
		return tagMap;
	}

	private void addTagToMap(String name, Tag tag) {
		if (tagMap == null) {
			tagMap = new HashMap<String, Tag>();
		}
		this.tagMap.put(name,  tag);
	}

	private CONTEXT getCurrentContext() {
		return currentContext;
	}

	private CONTEXT setCurrentContext(CONTEXT currentContext) {
		CONTEXT prev = this.currentContext;
		this.currentContext = currentContext;
		return prev;
	}

	public Map<String, String> getPreferences() {
		return preferences;
	}
	
	public String getPreference(String key) {
		if (preferences != null) {
			return preferences.get(key);
		}
		return null;
	}

	private void setPreferences(Map<String, String> preferences) {
		this.preferences = preferences;
	}

	private AnswerCurationManager getCurationMgr() {
		return curationMgr;
	}

	private void setCurationMgr(AnswerCurationManager curationMgr) {
		this.curationMgr = curationMgr;
	}

	@Override
	public void addCodeFile(File javaFile) {
		if (codeFiles == null) {
			codeFiles = new ArrayList<File>();
		}
		codeFiles.add(javaFile);
	}

	@Override
	public void addCodeFiles(List<File> javaFiles) {
		if (codeFiles != null) {
			codeFiles.addAll(javaFiles);
		}
		else {
			setCodeFiles(javaFiles);
		}
	}

	public List<File> getCodeFiles() {
		return codeFiles;
	}

	public void setCodeFiles(List<File> codeFiles) {
		this.codeFiles = codeFiles;
	}

	private OntModel getCodeModel() {
		return codeModel;
	}

	private void setCodeModel(OntModel codeModel) {
		this.codeModel = codeModel;
	}

	private String getCodeModelName() {
		return codeModelName;
	}

	private void setCodeModelName(String codeModelName) {
		this.codeModelName = codeModelName;
		getCurationMgr().getExtractionProcessor().setCodeModelName(codeModelName);
	}
	
	private String getCodeModelNamespace() {
		return codeModelName + "#";
	}

	public String getCodeModelPrefix() {
		return codeModelPrefix;
	}

	private void setCodeModelPrefix(String codeModelPrefix) {
		this.codeModelPrefix = codeModelPrefix;
	}

	public String getDefaultCodeModelName() {
		if (defaultCodeModelName == null) {
			defaultCodeModelName = "http://com.ge.reasearch.sadl.darpa.aske.answer/DefaultModelName";
		}
		return defaultCodeModelName;
	}

	public void setDefaultCodeModelName(String defCodeModelName) {
		this.defaultCodeModelName = codeModelName;
		getCurationMgr().getExtractionProcessor().setCodeModelName(codeModelName);
	}

	public String getDefaultCodeModelPrefix() {
		if (defaultCodeModelPrefix == null) {
			defaultCodeModelPrefix = "defmdlnm";
		}
		return defaultCodeModelPrefix;
	}

	public void setDefaultCodeModelPrefix(String codeModelPrefix) {
		this.defaultCodeModelPrefix = codeModelPrefix;
	}

	private void addImportToJenaModel(String modelName, String importUri, String importPrefix, Model importedOntModel) {
		getCodeModel().getDocumentManager().addModel(importUri, importedOntModel, true);
		Ontology modelOntology = getCodeModel().createOntology(modelName);
		if (importPrefix != null) {
			getCodeModel().setNsPrefix(importPrefix, importUri);
		}
		com.hp.hpl.jena.rdf.model.Resource importedOntology = getCodeModel().createResource(importUri);
		modelOntology.addImport(importedOntology);
		getCodeModel().addSubModel(importedOntModel);
		getCodeModel().addLoadedImport(importUri);
	}

	private String getCodeMetaModelUri() {
		return codeMetaModelUri;
	}

	private void setCodeMetaModelUri(String codeMetaModelUri) {
		this.codeMetaModelUri = codeMetaModelUri;
	}

	private IConfigurationManagerForIDE getCodeMetaModelConfigMgr() {
		return codeMetaModelConfigMgr;
	}

	private void setCodeMetaModelConfigMgr(IConfigurationManagerForIDE codeMetaModelConfigMgr) {
		this.codeMetaModelConfigMgr = codeMetaModelConfigMgr;
	}

	private String getCodeMetaModelPrefix() {
		return codeMetaModelPrefix;
	}

	private void setCodeMetaModelPrefix(String codeMetaModelPrefix) {
		this.codeMetaModelPrefix = codeMetaModelPrefix;
	}

	private String getCodeMetaModelModelFolder() {
		return codeMetaModelModelFolder;
	}

	public void setCodeMetaModelModelFolder(String codeMetaModelModelFolder) {
		this.codeMetaModelModelFolder = codeMetaModelModelFolder;
	}

}