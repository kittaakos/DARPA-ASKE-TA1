/********************************************************************** 
 * Note: This license has also been called the "New BSD License" or 
 * "Modified BSD License". See also the 2-clause BSD License.
 *
 * Copyright � 2018-2019 - General Electric Company, All Rights Reserved
 * 
 * Projects: ANSWER and KApEESH, developed with the support of the Defense 
 * Advanced Research Projects Agency (DARPA) under Agreement  No.  
 * HR00111990006 and Agreement No. HR00111990007, respectively. 
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its 
 *    contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 * THE POSSIBILITY OF SUCH DAMAGE.
 *
 ***********************************************************************/
package com.ge.research.sadl.darpa.aske.processing.imports;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.ge.research.sadl.builder.ConfigurationManagerForIdeFactory;
import com.ge.research.sadl.builder.IConfigurationManagerForIDE;
import com.ge.research.sadl.darpa.aske.curation.AnswerCurationManager;
import com.ge.research.sadl.darpa.aske.processing.DialogConstants;
import com.ge.research.sadl.jena.JenaProcessorException;
import com.ge.research.sadl.jena.inference.SadlJenaModelGetterPutter;
import com.ge.research.sadl.processing.SadlConstants;
import com.ge.research.sadl.reasoner.CircularDependencyException;
import com.ge.research.sadl.reasoner.ConfigurationException;
import com.ge.research.sadl.reasoner.IConfigurationManagerForEditing.Scope;
import com.ge.research.sadl.reasoner.IReasoner;
import com.ge.research.sadl.reasoner.InvalidNameException;
import com.ge.research.sadl.reasoner.QueryCancelledException;
import com.ge.research.sadl.reasoner.QueryParseException;
import com.ge.research.sadl.reasoner.ReasonerNotFoundException;
import com.ge.research.sadl.reasoner.ResultSet;
import com.ge.research.sadl.reasoner.TranslationException;
import com.ge.research.sadl.reasoner.utils.SadlUtils;
import com.github.javaparser.JavaParser;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
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
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;
import com.ibm.icu.text.PluralRules.Operand;

public class JavaModelExtractorJP implements IModelFromCodeExtractor {
    private static final Logger logger = Logger.getLogger (JavaModelExtractorJP.class) ;
	private String packageName = "";
	private AnswerCurationManager curationMgr = null;
	
	public enum USAGE {Defined, Used, Reassigned}
	public enum InputOutput {Input, Output}
	private Map<String, String> preferences;
	private List<File> codeFiles;
	
	// Assumption: the code meta model and the code model extracted are in the same codeModelFolder (same kbase)
	private IConfigurationManagerForIDE codeModelConfigMgr;	// the ConfigurationManager used to access the code extraction model
	private String codeMetaModelUri;	// the name of the code extraction metamodel
	private String codeMetaModelPrefix;	// the prefix of the code extraction metamodel
	private OntModel codeModel;
	private Map<String,OntModel> codeModels = null;
	private String codeModelName;	// the name of the model  being created by extraction
	private String codeModelPrefix; // the prefix of the model being created by extraction

	private Individual rootContainingInstance = null;
	private boolean includeSerialization = true;
	private String defaultCodeModelName = null;
	private String defaultCodeModelPrefix = null;
	private Map<Node, Individual> postProcessingList = new HashMap<Node, Individual>(); // key is the MethodCallExpr
																						// value is the calling method instance
	private Map<Node,Individual> methodsFound = new HashMap<Node, Individual>();
	private Individual methodWithBodyInProcess = null;
	private boolean sendCommentsToTextService = false;	// change to true to send comments to text-to-triples service
	private Map<Individual, LiteralExpr> potentialConstants = new HashMap<Individual, LiteralExpr>();
	private List<Individual> discountedPotentialConstants = new ArrayList<Individual>();
	private List<String> classesToIgnore = new ArrayList<String>();
	private List<String> primitiveTypesList = null;
	
	public JavaModelExtractorJP(AnswerCurationManager acm, Map<String, String> preferences) {
		setCurationMgr(acm);
		this.setPreferences(preferences);
//	    logger.setLevel(Level.ALL);
	}
	
	private void initializeContent(String modelName, String modelPrefix) {
		packageName = "";
		postProcessingList.clear();
		if (getCodeModelName() == null) {	// don't override a preset model name
			setCodeModelName(modelName);
		}
		if (getCodeModelPrefix() == null) {
			setCodeModelPrefix(modelPrefix);	// don't override a preset model name		
		}
	}

	public boolean process(String inputIdentifier, String content, String modelName, String modelPrefix) throws ConfigurationException, IOException {
	    initializeContent(modelName, modelPrefix);
		String defName = getCodeModelName() + "_comments";
		getCurationMgr().getTextProcessor().setTextModelName(defName);
		String defPrefix = getCodeModelPrefix() + "_cmnt";
		getCurationMgr().getTextProcessor().setTextModelPrefix(defPrefix);
		parse(inputIdentifier, getCurationMgr().getOwlModelsFolder(), content);
		try {
			addConstants();
		} catch (TranslationException e) {
			throw new IOException(e.getMessage(), e);
		}
		return true;
	}
	
	private void addConstants() throws TranslationException {
		if (potentialConstants.size() > 0) {
			if (discountedPotentialConstants.size() > 0) {
				for (Individual inst : discountedPotentialConstants) {
					potentialConstants.remove(inst);
				}
			}
			logger.debug("Constants:");
			for (Individual inst : potentialConstants.keySet()) {
				LiteralExpr value = potentialConstants.get(inst);
				logger.debug("   " + inst.getLocalName() + "=" + value.toString());
				Literal lval;
				if (value instanceof IntegerLiteralExpr) {
					lval = SadlUtils.getLiteralMatchingDataPropertyRange(getCurrentCodeModel(),XSD.xint.getURI(), value.toString());
				}
				else if (value instanceof DoubleLiteralExpr) {
					lval = SadlUtils.getLiteralMatchingDataPropertyRange(getCurrentCodeModel(),XSD.xdouble.getURI(), value.toString());
				}
				else if (value instanceof LongLiteralExpr) {
					lval = SadlUtils.getLiteralMatchingDataPropertyRange(getCurrentCodeModel(),XSD.xlong.getURI(), value.toString());
				}
				else if (value instanceof BooleanLiteralExpr) {
					lval = SadlUtils.getLiteralMatchingDataPropertyRange(getCurrentCodeModel(),XSD.xboolean.getURI(), value.toString());
				}
				else {
					lval = SadlUtils.getLiteralMatchingDataPropertyRange(getCurrentCodeModel(),XSD.xstring.getURI(), value.toString());
				}
				inst.addRDFType(getConstantVariableClass());
				Individual uq = createUnittedQuantity(lval, null);
				inst.addProperty(getConstantValueProperty(), uq);
			}
		}
	}

	private Individual createUnittedQuantity(Literal lval, String unit) {
		OntClass uqcls = getUnittedQuantityClass();
		Individual uqinst = getCurrentCodeModel().createIndividual(uqcls);
		uqinst.addProperty(getUnittedQuantityValueProperty(), lval);
		if (unit != null) {
			uqinst.addProperty(getUnittedQuantityUnitProperty(), unit);
		}
		return uqinst;
	}

	//use ASTParse to parse string
	private void parse(String inputIdentifier, String modelFolder, String javaCodeContent) throws IOException, ConfigurationException {
		try {
			String source = null;
			if (inputIdentifier.lastIndexOf('/') > 0) {
				source = inputIdentifier.substring(inputIdentifier.lastIndexOf('/') + 1);
			}
			else if (inputIdentifier.lastIndexOf('\\') > 0) {
				source = inputIdentifier.substring(inputIdentifier.lastIndexOf('\\') + 1);
			}
			else {
				source = inputIdentifier;
			}
			String msg = "Parsing code file '" + source + "'.";
			getCurationMgr().notifyUser(modelFolder, msg, true);
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("***************** code to process ******************");
			logger.debug(javaCodeContent);
			logger.debug("****************************************************");
		}
				
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
        initializeCodeModel(getCurationMgr().getOwlModelsFolder());
        findAllClassesToIgnore(cu);
        processBlock(cu, null);
        postProcess();
	}

	private void postProcess() {
		Iterator<Node> nitr = postProcessingList.keySet().iterator();
		while (nitr.hasNext()) {
			Node node = nitr.next();
			Individual callingMethod = postProcessingList.get(node);
			if (node instanceof MethodCallExpr && ignoreMethodCall((MethodCallExpr) node, callingMethod)) {
				continue;
			}
			Node containerNode = null;
			if (node instanceof MethodCallExpr) {
				// node is the MethodCallExpr
				Optional<Node> optContainer = ((MethodCallExpr)node).getParentNode();
				if (optContainer.isPresent()) {
					containerNode = optContainer.get();
				}
				NodeList<Expression> args = ((MethodCallExpr)node).getArguments();
				String methName = ((MethodCallExpr)node).getNameAsString();
				Individual methodCalled = findMethodCalled((MethodCallExpr)node);
				// create instance of MethodCall that will contain details of this call
				Individual methodCall = getCurrentCodeModel().createIndividual(getMethodCallClass());
//				methodCalled.addProperty(getCallsProperty(), methodCall);
//				methodCall.addProperty(getCallsProperty(), methodCalled);
//				methodCall.addProperty(getCodeBlockProperty(), cb);
				callingMethod.addProperty(getCallsProperty(), methodCall);
				methodCall.addProperty(getCodeBlockProperty(), methodCalled);
				
				addRange(methodCall, node);

				addInputMapping(callingMethod, methodCall, methodCalled, (MethodCallExpr)node);
				addReturnedMapping(callingMethod, methodCall, methodCalled, containerNode, (MethodCallExpr)node);
			}
			else {
				logger.debug("Unexprected node type in postProcessingList: " + node.getClass().getCanonicalName());
			}
		}
	}

	private boolean addRange(Individual blockInst, Node blockNode) {
		Optional<Range> rng = blockNode.getRange();
		if (rng.isPresent()) {
			blockInst.addProperty(getBeginsAtProperty(), getCurrentCodeModel().createTypedLiteral(rng.get().begin.line));
			blockInst.addProperty(getEndsAtProperty(), getCurrentCodeModel().createTypedLiteral(rng.get().end.line));
			return true;
		}
		return false;
	}

	private void addReturnedMapping(Individual methodCalling, Individual methodCall, Individual methodCalled, Node containerNode, MethodCallExpr node) {
		if (containerNode instanceof AssignExpr) {
			Expression assignedTo = ((AssignExpr)containerNode).getTarget();
			if (assignedTo instanceof NameExpr) {
				// Java can only return a single value
				StmtIterator stmtitr = getCurrentCodeModel().listStatements(null, getCodeBlockProperty(), methodCalled);
				while (stmtitr.hasNext()) {
					Resource ref = stmtitr.next().getSubject();
					Statement outputstmt = ref.getProperty(getOutputProperty());
					if (outputstmt != null) {
						RDFNode output = outputstmt.getObject();
						if (output != null && output.isLiteral() && output.asLiteral().getValue().equals(true)) {
							StmtIterator stmtitr2 = getCurrentCodeModel().listStatements(null, getReferenceProperty(), ref);
							while (stmtitr2.hasNext()) {
								Resource cv = stmtitr2.next().getSubject();
//								System.out.println("\nSetting output mapping for " + cv.toString());
								Individual mapping = getCurrentCodeModel().createIndividual(getOutputMappingClass());
								mapping.addProperty(getCallingVariableProperty(), findDefinedVariable(((NameExpr)assignedTo).getNameAsString(), methodCalling));
								mapping.addProperty(getCalledVariableProperty(), cv);
								methodCall.addProperty(getReturnedMappingProperty(), mapping);
							}
						}
					}
//					else {
//						StmtIterator stmtitr2 = getCurrentCodeModel().listStatements(null, getReferenceProperty(), ref);
//						while (stmtitr2.hasNext()) {
//							Resource cv = stmtitr2.next().getSubject();
//							System.out.println("\nCodeVariable: " + cv.toString());
//						}
//						StmtIterator stmtitr3 = ref.listProperties();
//						while (stmtitr3.hasNext()) {
//							System.out.println(stmtitr3.next().toString());
//						}
//					}
				}
			}
		}
	}

	private void addInputMapping(Individual methodCalling, Individual methodCall, Individual methodCalled, MethodCallExpr node) {
		Iterator<Expression> argsitr = node.getArguments().iterator();
		List<Individual> argVars = null;
		Statement argsstmt = methodCalled.getProperty(getArgumentsProperty());
		if (argsstmt != null) {
			RDFNode argsList = argsstmt.getObject();
			if (argsList.canAs(Individual.class)) {
				argVars = getMembersOfList(getCurrentCodeModel(), argsList.as(Individual.class));
			}
		}
		if (argVars != null) {
			Iterator<Individual> argvaritr = argVars.iterator();
			while (argsitr.hasNext() && argvaritr.hasNext()) {
				Individual argvar = argvaritr.next();
				Expression argexpr = argsitr.next();
				if (argexpr instanceof NameExpr) {
					Individual mapping = getCurrentCodeModel().createIndividual(getInputMappingClass());
					mapping.addProperty(getCallingVariableProperty(), findDefinedVariable(((NameExpr)argexpr).getNameAsString(), methodCalling));
					mapping.addProperty(getCalledVariableProperty(), argvar);
					methodCall.addProperty(getInputMappingProperty(), mapping);
				}
				else {
					
				}
			}
		}
	}

	private Individual findMethodCalled(MethodCallExpr node) {
		Iterator<Node> nitr = methodsFound.keySet().iterator();
		while (nitr.hasNext()) {
			Node meth = nitr.next();
			if (meth instanceof MethodDeclaration && ((MethodDeclaration) meth).getNameAsString().equals(node.getNameAsString())) {
				return methodsFound.get(meth);
			}
		}
		// external reference; create instance.
		String methName = node.getNameAsString();
		String extUri = "";
		Optional<Expression> scope = node.getScope();
		if (scope.isPresent()) {
			extUri += scope.get().toString();
			extUri += ".";
		}
		extUri += methName;
		extUri = getCodeModelNamespace() + extUri;
		return getCurrentCodeModel().createIndividual(extUri, getExternalModelClass());
	}

	@Override
	public void setIncludeSerialization(boolean includeSerialization) {
		this.includeSerialization = includeSerialization;
	}

	@Override
	public boolean isIncludeSerialization() {
		return includeSerialization;
	}

	private Resource getExternalModelClass() {
		return getCurrentCodeModel().getOntClass(getCodeMetaModelUri() + "#ExternalMethod");
	}
	
	private Resource getMethodCallClass() {
		return getCurrentCodeModel().getOntClass(getCodeMetaModelUri() + "#MethodCall");
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
			
			setCodeModelConfigMgr(ConfigurationManagerForIdeFactory.getConfigurationManagerForIDE(extractionMetaModelModelFolder, null)); //getCurationMgr().getProjectConfigurationManager());
			OntDocumentManager owlDocMgr = getCodeModelConfigMgr().getJenaDocumentMgr();
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
			setCurrentCodeModel(ModelFactory.createOntologyModel(spec));	
			getCurrentCodeModel().setNsPrefix(getCodeModelPrefix(), getCodeModelNamespace());
			Ontology modelOntology = getCurrentCodeModel().createOntology(getCodeModelName());
			logger.debug("Ontology '" + getCodeModelName() + "' created");
			modelOntology.addComment("This ontology was created by extraction from code by the ANSWER JavaModelExtractorJP.", "en");
			setCodeMetaModelUri(DialogConstants.CODE_EXTRACTION_MODEL_URI);
			setCodeMetaModelPrefix(DialogConstants.CODE_EXTRACTION_MODEL_PREFIX);
			OntModel importedOntModel = getCodeModelConfigMgr().getOntModel(getCodeMetaModelUri(), Scope.INCLUDEIMPORTS);
			addImportToJenaModel(getCodeModelName(), getCodeMetaModelUri(), getCodeMetaModelPrefix(), importedOntModel);
			OntModel sadlImplicitModel = getCodeModelConfigMgr().getOntModel(getSadlImplicitModelUri(), Scope.INCLUDEIMPORTS);
			addImportToJenaModel(getCodeModelName(), getSadlImplicitModelUri(), 
					getCodeModelConfigMgr().getGlobalPrefix(getSadlImplicitModelUri()), sadlImplicitModel);
			OntModel sadlListModel = getCodeModelConfigMgr().getOntModel(getSadlListModelUri(), Scope.INCLUDEIMPORTS);
			addImportToJenaModel(getCodeModelName(), getSadlListModelUri(), 
					getCodeModelConfigMgr().getGlobalPrefix(getSadlListModelUri()), sadlListModel);
			OntClass ctic = getClassesToIgnoreClass();
			if (ctic != null) {
				ExtendedIterator<OntClass> extitr = ctic.listSubClasses();
				if (extitr != null && extitr.hasNext()) {
					while (extitr.hasNext()) {
						Resource cti = extitr.next();
						classesToIgnore.add(cti.getLocalName());
					}
					extitr.close();
				}
			}
		}
		else {
			setCurrentCodeModel(getCurationMgr().getExtractionProcessor().getCodeModel());
		}
		
	}

	private void findAllClassesToIgnore(Node node) {
		if (node instanceof ClassOrInterfaceDeclaration) {
			ClassOrInterfaceDeclaration cls = (ClassOrInterfaceDeclaration) node;
			Iterator<ClassOrInterfaceType> etitr = cls.getExtendedTypes().iterator();
			while (etitr.hasNext()) {
				ClassOrInterfaceType et = etitr.next();
				if (classesToIgnore.contains(et.getNameAsString())) {
					if (!classesToIgnore.contains(cls.getNameAsString())) {
						classesToIgnore.add(cls.getNameAsString());
						logger.debug("findAllClassesToIgnore added '" + cls.getNameAsString() + "' to classesToIgnore");
					}
				}
				else {
					findAllClassesToIgnore(et);
				}
			}
		}
		else if (node instanceof ImportDeclaration) {
			return;
		}
		else if (node instanceof Modifier) {
			return;
		}
		else if (node instanceof ClassOrInterfaceType) {
			return;
		}
		else if (node instanceof MethodDeclaration) {
			return;
		}
		else if (node instanceof ConstructorDeclaration) {
			return;
		}
		else if (node instanceof FieldDeclaration) {
			return;
		}
		else if (node instanceof FieldAccessExpr) {
			return;
		}
		else if (node instanceof VariableDeclarator) {
			return;
		}
		else if (node instanceof ObjectCreationExpr) {
			return;
		}
		else if (node instanceof MethodCallExpr) {
			return;
		}
		else if (node instanceof AssignExpr) {
			return;
		}
		List<Node> childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.size(); i++) {
			Node childNode = childNodes.get(i);
			findAllClassesToIgnore(childNode);
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
			if (!ignoreClass(cls, containingInst)) {
				Individual blkInst = getOrCreateClass(cls);
				if (containingInst != null) {
					getCurrentCodeModel().add(blkInst, getContainedInProperty(), containingInst);
				}
				else {
					setRootContainingInstance(blkInst);
				}
				processBlock(cls, blkInst);
				addSerialization(blkInst, cls.toString());
			}
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
			String rt = m.getTypeAsString();
			if (rt == null || !ignoreClass(rt, containingInst, false)) {
				Comment cmnt = getComment(m);
				Individual methInst = getOrCreateMethod(m, containingInst);
				methodsFound.put(m, methInst);
				if (containingInst != null) {
					getCurrentCodeModel().add(methInst, getContainedInProperty(), containingInst);
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
					} catch (AnswerExtractionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (argList != null) {
					try {
						Individual typedList = addMembersToList(getCurrentCodeModel(), null, getCodeVariableListClass(), getCodeVariableClass(), argList.iterator());
						methInst.addProperty(getArgumentsProperty(), typedList);
					} catch (JenaProcessorException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (TranslationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				addRange(methInst, m);
				Individual prior = setMethodWithBodyInProcess(methInst);
				processBlock(m, methInst);	// order matters--do this after parameters and before return
				setMethodWithBodyInProcess(prior);
				
				if (rt != null) {
					List<Literal> rtypes = new ArrayList<Literal>();
					rtypes.add(getCurrentCodeModel().createTypedLiteral(rt));
					Individual returnTypes;
					try {
						returnTypes = addMembersToList(getCurrentCodeModel(), null, getStringListClass(), XSD.xstring, rtypes.iterator());
						methInst.addProperty(getReturnTypeProperty(), returnTypes);
					} catch (JenaProcessorException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (TranslationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				logger.debug(methInst.getURI() + " returns " + ((rt != null && rt.length() > 0) ? rt : "void"));
				addSerialization(methInst, ((MethodDeclaration) childNode).toString());
			}
		}
		else if (childNode instanceof MethodCallExpr) {
			MethodCallExpr mc = (MethodCallExpr)childNode;
			if (!ignoreMethodCall(mc, containingInst)) {
				addNodeToPostProcessingList(mc, containingInst);
	        	NodeList<Expression> args = mc.getArguments();
	        	Iterator<Expression> nlitr = args.iterator();
	        	while (nlitr.hasNext()) {
	        		Expression expr = nlitr.next();
	        		if (expr instanceof NameExpr) {
	        			try {
							setSetterArgument(mc, (NameExpr)expr, containingInst);
						} catch (AnswerExtractionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	        		}
	        		else {
	           			processBlockChild(expr, containingInst, USAGE.Defined);        			
	        		}
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
			else if (expr instanceof MethodCallExpr) {
				processBlockChild(expr, containingInst, USAGE.Used);
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
			} catch (AnswerExtractionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else if (childNode instanceof VariableDeclarator) {
			try {
				OntClass codeVarClass = getClassFieldClass();	// TODO is this always field or only in a class?
				Individual fdInst = getOrCreateCodeVariable(childNode, containingInst, codeVarClass);
			} catch (AnswerExtractionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (childNode instanceof AssignExpr) {
			AssignExpr ass = (AssignExpr)childNode;
			List<Node> assChildren = ass.getChildNodes();
			if (assChildren.size() == 2) {
				Node n0 = assChildren.get(0);
				Node n1 = assChildren.get(1);
				if (n0 instanceof NameExpr) {
					String nm = ((NameExpr)n0).getNameAsString();
					Individual varInst = findDefinedVariable(nm, containingInst);
					if (n1 instanceof LiteralExpr) {
						if (varInst != null) {
							logger.debug(varInst.getLocalName() + ": " + ass.toString());
							addPotentialConstant(varInst, (LiteralExpr)n1);
						}
					}
					else {
						removePotentalConstant(varInst);
					}
				}
			}
			for (int j = 0; j < assChildren.size(); j++) {
				processBlockChild(assChildren.get(j), containingInst, (j == 0 ? USAGE.Reassigned : USAGE.Used));					
			}
		}
		else if (childNode instanceof BinaryExpr) {
			Operator op = ((BinaryExpr)childNode).getOperator();
			if (op.equals(Operator.DIVIDE) ||
					op.equals(Operator.MINUS) ||
					op.equals(Operator.MULTIPLY) ||
					op.equals(Operator.PLUS) ||
					op.equals(Operator.REMAINDER) ||
					op.equals(Operator.SIGNED_RIGHT_SHIFT) ||
					op.equals(Operator.UNSIGNED_RIGHT_SHIFT)
					) {
				containingInst.setPropertyValue(getDoesComputationProperty(), getCurrentCodeModel().createTypedLiteral(true));
				logger.debug("BinaryExpr: " + ((BinaryExpr)childNode).toString());
			}
			processBlock(childNode, containingInst);
		}
		else if (childNode instanceof NameExpr) {
			String nm = ((NameExpr)childNode).getNameAsString();
			findCodeVariableAndAddReference(childNode, nm, containingInst, knownUsage, true, null, false);
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
			if (!returnChildren.isEmpty()) {
				Node ret0 = returnChildren.get(0);
				if (ret0 instanceof EnclosedExpr) {
					ret0 = ((EnclosedExpr)ret0).getInner();
				}
				if (ret0 instanceof NameExpr) {
					// this is an output of this block. It should already exist.
					String nm = ((NameExpr)ret0).getNameAsString();
					findCodeVariableAndAddReference(ret0, nm, containingInst, USAGE.Used, true, InputOutput.Output, false);
				}
				else {
					for (int j = 0; j < returnChildren.size(); j++) {
						processBlockChild(returnChildren.get(j), containingInst, null);
					}
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
//			logger.debug("Ignoring '" + childNode.toString() + "'");
//		}
		else {
			logger.debug("Block child unhandled Node '" + childNode.toString().trim() + "' of type " + childNode.getClass().getCanonicalName());
		}
		investigateComments(childNode, containingInst);
	}

	private boolean ignoreMethodCall(MethodCallExpr mc, Individual containingInst) {
		if (mc.getScope().isPresent()) {
			Expression scope = mc.getScope().get();
			if (scope instanceof FieldAccessExpr) {
				if (ignoreMethod((FieldAccessExpr)scope, containingInst)) {
					return true;
				}
			}
			else if (scope instanceof NameExpr) {
				RDFNode tvtype = findDefinedVariableType(((NameExpr)scope).getNameAsString(), containingInst);
				if (ignoreType(tvtype, containingInst)) {
					return true;
				}
			}
		}
		// consider the type returned and if ignored class, ignore
		if (mc.getParentNode().get() instanceof AssignExpr) {
			AssignExpr ae = (AssignExpr) mc.getParentNode().get();
			if (ae.getTarget() instanceof NameExpr) {
				NameExpr aene = (NameExpr)ae.getTarget();
				RDFNode tvtype = findDefinedVariableType(aene.getNameAsString(), containingInst);
				if (ignoreType(tvtype, containingInst)) {
					return true;
				}
			}
		}
		// consider the arguments and if ignored types ignore
		Iterator<Expression> argitr = mc.getArguments().iterator();
		while (argitr.hasNext()) {
			Expression arg = argitr.next();
			if (arg instanceof NameExpr) {
				RDFNode typ = findDefinedVariableType(((NameExpr)arg).getNameAsString(), containingInst);
				if (ignoreType(typ, containingInst)) {
					return true;
				}
			}
			else if (arg instanceof ObjectCreationExpr) {
				ClassOrInterfaceType typ = ((ObjectCreationExpr)arg).getType();
				if (ignoreClass(typ.getNameAsString(), containingInst, false)) {
					return true;
				}
			}
			else if (arg instanceof FieldAccessExpr) {
				if (ignoreMethod((FieldAccessExpr)arg, containingInst)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean ignoreMethod(FieldAccessExpr fae, Individual containingInst) {
		Expression scope = fae.getScope();
		if (scope instanceof NameExpr) {
			if (ignoreClass(((NameExpr)scope).getNameAsString(), containingInst, true)) {
				return true;
			}
		}
		else if (scope instanceof FieldAccessExpr) {
			if (ignoreMethod((FieldAccessExpr)scope, containingInst)) {
				return true;
			}
		}
		return false;
	}

	private boolean ignoreType(RDFNode tvtype, Individual containingInst) {
		if (tvtype == null) {
			// if the variable doesn't exist at this point it must have been an ignored type
			return true;					
		}
		if (tvtype.isLiteral()) {
			if (ignoreClass(tvtype.asLiteral().getValue().toString(), containingInst, true)) {
				return true;
			}
		}
		else if (tvtype.isURIResource()) {
			if (ignoreClass(tvtype.asResource().getLocalName(), containingInst, true)) {
				return true;
			}
		}
		return false;
	}
	
	private RDFNode findDefinedVariableType(String nm, Individual containingInst) {
		RDFNode nmType = null;
		Individual targetVar = findDefinedVariable(nm, containingInst);
		if (targetVar != null) {
			nmType = targetVar.getPropertyValue(getVarTypeProperty());
		}
		return nmType;
	}

	private boolean ignoreClass(ClassOrInterfaceDeclaration cls, Individual containingInst) {
		if (ignoreClass(cls.getName().asString(), containingInst, false)) {
			return true;
		}
		return false;
	}
	
	private boolean ignoreClass(String clsname, Individual containingInst, boolean nullIsIgnore) {
		if (classesToIgnore.contains(clsname)) {
			return true;
		}
		OntClass cls = getCurrentCodeModel().getOntClass(getCodeModelNamespace() + clsname);
		if (cls != null) {
			ExtendedIterator<OntClass> extitr = cls.listSuperClasses();
			while (extitr.hasNext()) {
				if (classesToIgnore.contains(extitr.next().getLocalName())) {
					extitr.close();
					return true;
				}
			}
		}
		else if (containingInst != null && !isBuiltinType(clsname)) {
			RDFNode tvtype = findDefinedVariableType(clsname, containingInst);
			if ((nullIsIgnore || tvtype != null) && ignoreType(tvtype, containingInst)) {
				return true;
			}
		}
		return false;
	}

	private boolean isBuiltinType(String clsname) {
		String[] builtinTypes = {"byte", "short", "int", "long", "float", "double", "char",
				"String", "boolean", 
				"Byte", "Short", "Integer", "Long", "Float", "Double", "Character",
				"Boolean"};
		if (primitiveTypesList == null) {
			primitiveTypesList = Arrays.asList(builtinTypes);
		}
		if (primitiveTypesList .contains(clsname)) {
			return true;
		}
		return false;
	}

	private void removePotentalConstant(Individual varInst) {
		if (!discountedPotentialConstants.contains(varInst)) {
			discountedPotentialConstants.add(varInst);
		}
	}

	private void addPotentialConstant(Individual varInst, LiteralExpr n1) {
		if (potentialConstants.containsKey(varInst)) {
			removePotentalConstant(varInst);
		}
		else {
			potentialConstants.put(varInst, n1);
		}
	}

	/**
	 * Method to add a method call to the post-processing list. 
	 * @param expr -- the MethodCallExpr
	 * @param codeBlock -- the code block (method) in which the call occurs
	 */
	private void addNodeToPostProcessingList(Node expr, Individual codeBlock) {
		postProcessingList.put(expr, codeBlock);
	}

	private void addSerialization(Individual blkInst, String code) {
		if (isIncludeSerialization()) {
			blkInst.addProperty(getSerializationProperty(), getCurrentCodeModel().createTypedLiteral(code));
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
				logger.debug("Found orphaned comment at line " + rng.get().getLineCount() + "(" + rng.get().begin.toString() + " to " + rng.get().end.toString() + ")");
			}
			else {
				logger.debug("Found orphaned comment but range not known");
			}
			logger.debug("   " + cmt.getContent());
		}
	}

	private void investigateComment(Node childNode, Individual subject, Comment cmt) {
		if (cmt != null) {
			logger.debug("   " + cmt.getContent());
			Individual cmtInst = getCurrentCodeModel().createIndividual(getCommentClass());
			if (subject == null) {
				subject = rootContainingInstance;
			}
			if (sendCommentsToTextService ) {
				String locality = null;
				String inputIdentifier = "CodeComments";
				int[] tpresult = null;
				try {
					tpresult = getCurationMgr().getTextProcessor().processText(inputIdentifier, cmt.getContent(), locality, getCodeModelPrefix());
				} catch (ConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				logger.debug("Text: " + cmt.getContent());
				logger.debug("nc=" + tpresult[0] + ", neq=" + tpresult[1]);
			}

			if (subject != null) {
				subject.addProperty(getCommentProperty(), cmtInst);
				cmtInst.addProperty(getCommentContentProperty(), getCurrentCodeModel().createTypedLiteral(cmt.getContent()));
				addRange(cmtInst, childNode);
			}
			else {
				logger.debug("Unable to add comment because there is no known subject");
			}
			Optional<Range> rng = childNode.getRange();
			if (rng.isPresent()) {
				logger.debug("Found comment at line " + rng.get().getLineCount() + "(" + rng.get().begin.toString() + " to " + rng.get().end.toString() + ")");
			}
			else {
				logger.debug("Found comment but range not known");
			}
		}
	}

	private Property getInputProperty() {
		return getCurrentCodeModel().getOntProperty(getCodeMetaModelUri() + "#input");
	}

	private Property getOutputProperty() {
		return getCurrentCodeModel().getOntProperty(getCodeMetaModelUri() + "#output");
	}

	private Individual findCodeVariableAndAddReference(Node childNode, String nm, Individual containingInst, USAGE usage, boolean lookToLargerScope, InputOutput inputOutput, boolean isSetterArgument) {
		String nnm = constructNestedElementUri(childNode, nm);

		Individual varInst = lookToLargerScope ? findDefinedVariable(nm, containingInst) : null;
		if (varInst == null && !lookToLargerScope) {
			try {
				varInst = getOrCreateCodeVariable(childNode, nm, nnm, containingInst, getCodeVariableClass(childNode), inputOutput);
			} catch (AnswerExtractionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (varInst != null) {
			try {
				Individual ref = createReference(childNode, varInst, containingInst, usage != null ? usage : USAGE.Used);
	          	setInputOutputIfKnown(ref, inputOutput);
	          	if (isSetterArgument) {
	          		ref.setPropertyValue(getSetterArgumentProperty(), getCurrentCodeModel().createTypedLiteral(true));
	          	}
			} catch (AnswerExtractionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			logger.debug("NameExpr (" + nnm + ") not found; it should already exist!");
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

	/**
	 * Method to find a CodeVariable given its name and containing instance
	 * @param nm
	 * @param containingInst
	 * @return
	 */
	private Individual findDefinedVariable(String nm, Individual containingInst) {
		Individual varInst = null;
		if (containingInst != null) {
			Individual inst = containingInst;
			String nnm = getCodeModelNamespace() + inst.getLocalName() + "." + nm;
			varInst = getCurrentCodeModel().getIndividual(nnm);
			if (varInst == null) {
				RDFNode obj = containingInst.getPropertyValue(getContainedInProperty());
				if (obj != null && obj.isURIResource() && obj.canAs(Individual.class)) {
					return findDefinedVariable(nm, obj.as(Individual.class));
				}
			}
		}
		return varInst;
	}

	private void setSetterArgument(MethodCallExpr mc, NameExpr arg, Individual containingInst) throws AnswerExtractionException {
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
			Individual cvInst = findCodeVariableAndAddReference(mc, arg.getNameAsString(), containingInst, USAGE.Used, true, InputOutput.Output, true);
		}
	}

	private Individual createReference(Node varNode, Individual codeVarInst, Individual containingInst, USAGE usage) throws AnswerExtractionException {
		Individual ref = createNewReference(containingInst, varNode, usage);
		codeVarInst.addProperty(getReferenceProperty(), ref);
		return ref;
	}

//	private Individual createNewReference(Individual blkInst, int beginsAt, int endsAt, USAGE usage) throws CodeExtractionException {
	private Individual createNewReference(Individual blkInst, Node varNode, USAGE usage) throws AnswerExtractionException {
		Individual refInst = getCurrentCodeModel().createIndividual(getReferenceClass());
		if (blkInst != null) {
			refInst.addProperty(getCodeBlockProperty(), blkInst);
		}
		refInst.addProperty(getUsageProperty(), getUsageInstance(usage));
		addRange(refInst, varNode);
		return refInst;
	}

	private Individual getOrCreateMethod(MethodDeclaration m, Individual containingInst) {
		Individual methInst = null;
    	String nm = m.getNameAsString();
		String nnm = constructNestedElementUri(m, nm);
    	methInst = getCurrentCodeModel().getIndividual(getCodeModelNamespace() + nnm);
    	if (methInst == null) {
    		methInst = getCurrentCodeModel().createIndividual(getCodeModelNamespace() + nnm, getCodeBlockMethodClass());
    	}
		return methInst;
	}

	private Individual getOrCreateCodeVariable(Node varNode, Individual containingInst, OntClass codeVarClass) throws AnswerExtractionException {
		Individual cvInst = null;
		String origName = null;
		String nnm = null;
		if (varNode instanceof VariableDeclarator) {
			origName = ((VariableDeclarator)varNode).getNameAsString();
			if (containingInst != null) {
				nnm = constructNestedElementUri(varNode, origName);
			}
			else {
				nnm = origName;
			}
			codeVarClass = getClassFieldClass();
		}
		else if (varNode instanceof NameExpr) {
			origName = ((NameExpr)varNode).getNameAsString();
			if (containingInst != null) {
				nnm = constructNestedElementUri(varNode, origName);
			}
			else {
				nnm = origName;
			}
		}
		else if (varNode instanceof VariableDeclarationExpr) {
			NodeList<VariableDeclarator> vars = ((VariableDeclarationExpr)varNode).getVariables();
			for (int i = 0; i < vars.size(); i++) {
				if (i > 0) {
					logger.debug("Multiple vars (" + varNode.toString() + ") in VariableDeclarationExpr not current handled");
				}
				VariableDeclarator var = vars.get(i);
				origName = var.getNameAsString();
				if (containingInst != null) {
					nnm = constructNestedElementUri(varNode, origName);
				}
				else {
					nnm = origName;
				}
				codeVarClass = getMethodVariableClass();
				cvInst = getOrCreateCodeVariable(var, origName, nnm, containingInst, codeVarClass, null);
				if (vars.get(i).getInitializer().isPresent()) {
		          	createReference(var, cvInst, containingInst, USAGE.Reassigned);
				}
			}
		}
		else if (varNode instanceof Parameter) {
			NameExpr nm = ((Parameter)varNode).getNameAsExpression();
			return findCodeVariableAndAddReference(varNode, nm.getNameAsString(), containingInst, USAGE.Defined, false, InputOutput.Input, false);
		}
		else {
			throw new AnswerExtractionException("Unexpected CodeVariable varNode type: " + varNode.getClass().getCanonicalName());
		}
		cvInst = getOrCreateCodeVariable(varNode, origName, nnm, containingInst, codeVarClass, null);
		return cvInst;
	}

	private Individual getOrCreateCodeVariable(Node varNode, String origName, String nnm, Individual containingInst,
			OntClass codeVarClass, InputOutput inputOutput) throws AnswerExtractionException {
      	String typeStr = null;
      	if (varNode instanceof VariableDeclarator) {
      		typeStr = ((VariableDeclarator)varNode).getTypeAsString();
      	}
      	else if (varNode instanceof Parameter) {
      		typeStr = ((Parameter)varNode).getTypeAsString();
      	}
      	else if (varNode instanceof VariableDeclarationExpr) {
      		typeStr = ((VariableDeclarationExpr)varNode).getVariable(0).getTypeAsString();
      	}
      	else {
      		int i = 0;
      	}
		Individual cvInst = null;
     	if (typeStr != null) {
      		if (!ignoreClass(typeStr, containingInst, false)) {
       			cvInst = getCurrentCodeModel().getIndividual(getCodeModelNamespace() + nnm);
      			if (cvInst == null && codeVarClass != null) {
      				cvInst = getCurrentCodeModel().createIndividual(getCodeModelNamespace() + nnm, codeVarClass);
      	          	Individual ref = createReference(varNode, cvInst, containingInst, USAGE.Defined);
      	          	setInputOutputIfKnown(ref, inputOutput);
      	          	cvInst.addProperty(getVarNameProperty(), getCurrentCodeModel().createTypedLiteral(origName));
      			}
      			cvInst.addProperty(getVarTypeProperty(), getCurrentCodeModel().createTypedLiteral(typeStr));
      		}
      	}
		return cvInst;
	}

	private void setInputOutputIfKnown(Individual ref, InputOutput inputOutput) {
		if (inputOutput != null) {
			if (inputOutput.equals(InputOutput.Input)) {
				ref.addProperty(getInputProperty(), getCurrentCodeModel().createTypedLiteral(true));
			}
			else if (inputOutput.equals(InputOutput.Output)) {
				ref.addProperty(getOutputProperty(), getCurrentCodeModel().createTypedLiteral(true));
			}
		}
	}

	private String constructNestedElementUri(Node node, String nm) {
		StringBuilder sb = new StringBuilder();
		Optional<Node> parent = node.getParentNode();
		while (parent.isPresent()) {
			if (parent.get() instanceof MethodDeclaration) {
				sb.insert(0,((MethodDeclaration)parent.get()).getNameAsString() + ".");
			}
			else if (parent.get() instanceof ClassOrInterfaceDeclaration) {
				sb.insert(0, ((ClassOrInterfaceDeclaration)parent.get()).getNameAsString() + ".");
			}
			parent = parent.get().getParentNode();
			if (!parent.isPresent()) {
				break;
			}
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
    	String nmm = constructNestedElementUri(cls, nm);
    	clsInst = getCurrentCodeModel().getIndividual(getCodeModelNamespace() + nmm); //getCodeModelNamespace() + nm);
    	if (clsInst == null) {
    		clsInst = getCurrentCodeModel().createIndividual(getCodeModelNamespace() + nmm, getCodeBlockClassClass()); //getCodeModelNamespace() + nm, getCodeBlockClassClass());
    	}
    	return clsInst;
	}

	private Individual getUsageInstance(USAGE usage) throws AnswerExtractionException {
		if (usage.equals(USAGE.Defined)) {
			return getCurrentCodeModel().getIndividual(getCodeMetaModelUri() + "#Defined");
		}
		else if (usage.equals(USAGE.Used)) {
			return getCurrentCodeModel().getIndividual(getCodeMetaModelUri() + "#Used");
		}
		else if (usage.equals(USAGE.Reassigned)) {
			return getCurrentCodeModel().getIndividual(getCodeMetaModelUri() + "#Reassigned");
		}
		throw new AnswerExtractionException("Unexpected USAGE: " + usage.toString());
	}

	private Property getSetterArgumentProperty() {
		return getCurrentCodeModel().getOntProperty(getCodeMetaModelUri() + "#setterArgument");
	}

	private Property getUsageProperty() {
		return getCurrentCodeModel().getOntProperty(getCodeMetaModelUri() + "#usage");
	}

	private Property getCommentProperty() {
		return getCurrentCodeModel().getOntProperty(getCodeMetaModelUri() + "#comment");
	}

	private Property getCommentContentProperty() {
		return getCurrentCodeModel().getOntProperty(getCodeMetaModelUri() + "#commentContent");
	}

	private Property getSerializationProperty() {
		return getCurrentCodeModel().getOntProperty(getCodeMetaModelUri() + "#serialization");
	}
	
	private Property getArgumentsProperty() {
		return getCurrentCodeModel().getOntProperty(getCodeMetaModelUri() + "#cmArguments");
	}
	
	private Property getCallingVariableProperty() {
		return getCurrentCodeModel().getOntProperty(getCodeMetaModelUri() + "#callingVariable");
	}
	
	private Property getCalledVariableProperty() {
		return getCurrentCodeModel().getOntProperty(getCodeMetaModelUri() + "#calledVariable");
	}
	
	private OntClass getCodeVariableListClass() {
		Property argProp = getArgumentsProperty();
		StmtIterator stmtItr = getCurrentCodeModel().listStatements(argProp, RDFS.range, (RDFNode)null);
		if (stmtItr.hasNext()) {
			RDFNode rng = stmtItr.nextStatement().getObject();
			if (rng.asResource().canAs(OntClass.class)) {
				return rng.asResource().as(OntClass.class);
			}
		}
		return null;
	}
	
	private Property getCallsProperty() {
		return getCurrentCodeModel().getOntProperty(getCodeMetaModelUri() + "#calls");
	}

	private Property getBeginsAtProperty() {
		return getCurrentCodeModel().getOntProperty(getCodeMetaModelUri() + "#beginsAt");
	}

	private Property getEndsAtProperty() {
		return getCurrentCodeModel().getOntProperty(getCodeMetaModelUri() + "#endsAt");
	}

	private Property getVarNameProperty() {
		return getCurrentCodeModel().getOntProperty(getCodeMetaModelUri() + "#varName");
	}

	private Property getVarTypeProperty() {
		return getCurrentCodeModel().getOntProperty(getCodeMetaModelUri() + "#varType");
	}

	private Property getCodeBlockProperty() {
		return getCurrentCodeModel().getOntProperty(getCodeMetaModelUri() + "#codeBlock");
	}

	private Property getDoesComputationProperty() {
		return getCurrentCodeModel().getOntProperty(getCodeMetaModelUri() + "#doesComputation");
	}
	
	private Property getIncompleteInformationProperty() {
		return getCurrentCodeModel().getOntProperty(getCodeMetaModelUri() + "#incompleteInformation");
	}

	private Property getReferenceProperty() {
		return getCurrentCodeModel().getOntProperty(getCodeMetaModelUri() + "#reference");
	}

	private Property getContainedInProperty() {
		return getCurrentCodeModel().getOntProperty(getCodeMetaModelUri() + "#containedIn");
	}

	private Property getConstantValueProperty() {
		return getCurrentCodeModel().getOntProperty(getCodeMetaModelUri() + "#constantValue");
	}

	private Property getReturnTypeProperty() {
		return getCurrentCodeModel().getOntProperty(getCodeMetaModelUri() + "#cmReturnTypes");
	}

	private OntClass getStringListClass() {
		Property argProp = getReturnTypeProperty();
		StmtIterator stmtItr = getCurrentCodeModel().listStatements(argProp, RDFS.range, (RDFNode)null);
		if (stmtItr.hasNext()) {
			RDFNode rng = stmtItr.nextStatement().getObject();
			if (rng.asResource().canAs(OntClass.class)) {
				return rng.asResource().as(OntClass.class);
			}
		}
		return null;
	}
	
	private com.hp.hpl.jena.rdf.model.Resource getInputMappingClass() {
		return getCurrentCodeModel().getOntClass(getCodeMetaModelUri() + "#InputMapping");
	}

	private Property getInputMappingProperty() {
		return getCurrentCodeModel().getProperty(getCodeMetaModelUri() + "#inputMapping");
	}

	private com.hp.hpl.jena.rdf.model.Resource getOutputMappingClass() {
		return getCurrentCodeModel().getOntClass(getCodeMetaModelUri() + "#OutputMapping");
	}

	private Property getReturnedMappingProperty() {
		return getCurrentCodeModel().getProperty(getCodeMetaModelUri() + "#returnedMapping");
	}

	private com.hp.hpl.jena.rdf.model.Resource getReferenceClass() {
		return getCurrentCodeModel().getOntClass(getCodeMetaModelUri() + "#Reference");
	}

	private com.hp.hpl.jena.rdf.model.Resource getCodeBlockMethodClass() {
		return getCurrentCodeModel().getOntClass(getCodeMetaModelUri() + "#Method");
	}

	private OntClass getClassesToIgnoreClass() {
		return getCurrentCodeModel().getOntClass(getCodeMetaModelUri() + "#ClassesToIgnore");
	}
	
	private com.hp.hpl.jena.rdf.model.Resource getCodeBlockClassClass() {
		return getCurrentCodeModel().getOntClass(getCodeMetaModelUri() + "#Class");
	}

	private OntClass getMethodArgumentClass() {
		return getCurrentCodeModel().getOntClass(getCodeMetaModelUri() + "#MethodArgument");
	}

	private OntClass getCommentClass() {
		return getCurrentCodeModel().getOntClass(getCodeMetaModelUri() + "#Comment");
	}

	private OntClass getClassFieldClass() {
		return getCurrentCodeModel().getOntClass(getCodeMetaModelUri() + "#ClassField");
	}

	private OntClass getCodeVariableClass() {
		return getCurrentCodeModel().getOntClass(getCodeMetaModelUri() + "#CodeVariable");
	}
	
	private OntClass getUnittedQuantityClass() {
		return getCurrentCodeModel().getOntClass(getSadlImplicitModelUri() + "#UnittedQuantity");
	}

	private Property getUnittedQuantityUnitProperty() {
		return getCurrentCodeModel().getOntProperty(getSadlImplicitModelUri() + "#unit");
	}

	private Property getUnittedQuantityValueProperty() {
		return getCurrentCodeModel().getOntProperty(getSadlImplicitModelUri() + "#value");
	}

	private OntClass getMethodVariableClass() {
		return getCurrentCodeModel().getOntClass(getCodeMetaModelUri() + "#MethodVariable");
	}

	private Resource getConstantVariableClass() {
		return getCurrentCodeModel().getOntClass(getCodeMetaModelUri() + "#ConstantVariable");
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

	public String getPackageName() {
		return packageName;
	}

	private void setPackageName(String packageName) {
		this.packageName = packageName;
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
		if (!codeFiles.contains(javaFile)) {
			codeFiles.add(javaFile);
		}
	}

	@Override
	public void addCodeFiles(List<File> javaFiles) {
		if (codeFiles != null) {
			for (File f : javaFiles) {
				if (!codeFiles.contains(f)) {
					codeFiles.add(f);
				}
			}
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

	public OntModel getCurrentCodeModel() {
		return codeModel;
	}

	public void setCurrentCodeModel(OntModel codeModel) {
		this.codeModel = codeModel;
	}

	public String getCodeModelName() {
		return codeModelName;
	}

	public void setCodeModelName(String codeModelName) {
		if (codeModelName != null && !codeModelName.startsWith("http://")) {
			codeModelName = "http://" + codeModelName;
		}
		this.codeModelName = codeModelName;
	}
	
	@Override
	public String getCodeModelNamespace() {
		return codeModelName + "#";
	}

	@Override
	public String getCodeModelPrefix() {
		return codeModelPrefix;
	}

	public void setCodeModelPrefix(String codeModelPrefix) {
		this.codeModelPrefix = codeModelPrefix;
	}

	/**
	 * Method to add an import to the model identified by modelName
	 * @param modelName
	 * @param importUri
	 * @param importPrefix
	 * @param importedOntModel
	 */
	private void addImportToJenaModel(String modelName, String importUri, String importPrefix, Model importedOntModel) {
		getCurrentCodeModel().getDocumentManager().addModel(importUri, importedOntModel, true);
		Ontology modelOntology = getCurrentCodeModel().createOntology(modelName);
		if (importPrefix != null) {
			getCurrentCodeModel().setNsPrefix(importPrefix, ensureHashOnUri(importUri));
		}
		com.hp.hpl.jena.rdf.model.Resource importedOntology = getCurrentCodeModel().createResource(importUri);
		modelOntology.addImport(importedOntology);
		getCurrentCodeModel().addSubModel(importedOntModel);
		getCurrentCodeModel().addLoadedImport(importUri);
	}
	
	private String ensureHashOnUri(String uri) {
		if (!uri.endsWith("#")) {
			uri+= "#";
		}
		return uri;
	}

	private String getCodeMetaModelUri() {
		return codeMetaModelUri;
	}
	
	private String getSadlImplicitModelUri() {
		return "http://sadl.org/sadlimplicitmodel";
	}

	private String getSadlListModelUri() {
		return "http://sadl.org/sadllistmodel";
	}

	private void setCodeMetaModelUri(String codeMetaModelUri) {
		this.codeMetaModelUri = codeMetaModelUri;
	}

	public IConfigurationManagerForIDE getCodeModelConfigMgr() {
		return codeModelConfigMgr;
	}

	private void setCodeModelConfigMgr(IConfigurationManagerForIDE codeMetaModelConfigMgr) {
		this.codeModelConfigMgr = codeMetaModelConfigMgr;
	}

	private String getCodeMetaModelPrefix() {
		return codeMetaModelPrefix;
	}

	private void setCodeMetaModelPrefix(String codeMetaModelPrefix) {
		this.codeMetaModelPrefix = codeMetaModelPrefix;
	}

	@Override
	public Map<String,OntModel> getCodeModels() {
		return codeModels;
	}

	@Override
	public void addCodeModel(String key ,OntModel codeModel) {
		if (codeModels == null) {
			codeModels = new HashMap<String, OntModel>();
		}
		codeModels.put(key, codeModel);
	}

	@Override
	public OntModel getCodeModel(String key) {
		if (codeModels != null) {
			return codeModels.get(key);
		}
		return null;
	}

	private Individual getMethodWithBodyInProcess() {
		return methodWithBodyInProcess;
	}

	private Individual setMethodWithBodyInProcess(Individual methodWithBodyInProcess) {
		Individual prior = this.methodWithBodyInProcess;
		this.methodWithBodyInProcess = methodWithBodyInProcess;
		return prior;
	}
	
	@Override
	public ResultSet executeSparqlQuery(String query) throws ConfigurationException, ReasonerNotFoundException, IOException, InvalidNameException, QueryParseException, QueryCancelledException {
		query = SadlUtils.stripQuotes(query);
		IReasoner reasoner = getCodeModelConfigMgr().getReasoner();
		if (!reasoner.isInitialized()) {
			reasoner.setConfigurationManager(getCodeModelConfigMgr());
			reasoner.initializeReasoner(getCodeModelConfigMgr().getModelFolder(), getCodeModelName(), null);
		}
		query = reasoner.prepareQuery(query);
		ResultSet results =  reasoner.ask(query);
		return results;
	}
	

	/**
	 * Method to convert an Iterator over a List of values to a SADL Typed List in the provided model
	 * @param lastInst -- the list to which to add members
	 * @param cls -- the class of the SADL list
	 * @param type --  the type of the members of the list
	 * @param memberIterator -- Iterator over the values to add
	 * @return -- the list instance
	 * @throws JenaProcessorException
	 * @throws TranslationException
	 */
	protected Individual addMembersToList(OntModel model, Individual lastInst, OntClass cls,
			com.hp.hpl.jena.rdf.model.Resource type, Iterator<?> memberIterator) throws JenaProcessorException, TranslationException {
		if (lastInst == null) {
			lastInst = model.createIndividual(cls);
		}
		Object val = memberIterator.next();
		if (val instanceof Individual) {
			Individual listInst = (Individual) val;
			if (type.canAs(OntClass.class)) {
				ExtendedIterator<com.hp.hpl.jena.rdf.model.Resource> itr = listInst.listRDFTypes(false);
				boolean match = false;
				while (itr.hasNext()) {
					com.hp.hpl.jena.rdf.model.Resource typ = itr.next();
					if (typ.equals(type)) {
						match = true;
					} else {
						try {
							if (typ.canAs(OntClass.class) && SadlUtils.classIsSubclassOf(typ.as(OntClass.class), type.as(OntClass.class), true, null)) {
								match = true;
							}
						} catch (CircularDependencyException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (match) {
						break;
					}
				}
				if (!match) {
					throw new JenaProcessorException("The Instance '" + listInst.toString() + "' doesn't match the List type.");
				}
				model.add(lastInst, model.getProperty(SadlConstants.SADL_LIST_MODEL_FIRST_URI),
						listInst);
			} else {
				throw new JenaProcessorException("The type of the list could not be converted to a class.");
			}
		} else {
			Literal lval;
			if (val instanceof Literal) {
				lval = (Literal) val;
			}
			else {
				lval = SadlUtils.getLiteralMatchingDataPropertyRange(model,type.getURI(), val);
			}
			model.add(lastInst, model.getProperty(SadlConstants.SADL_LIST_MODEL_FIRST_URI), lval);
		}
		if (memberIterator.hasNext()) {
			Individual rest = addMembersToList(model, null, cls, type, memberIterator);
			model.add(lastInst, model.getProperty(SadlConstants.SADL_LIST_MODEL_REST_URI), rest);
		}
		return lastInst;
	}
	
	/** Method to return the members of a SADL typed list
	 * 
	 * @param model
	 * @param typedList
	 * @return
	 */
	protected List<Individual> getMembersOfList(OntModel model, Individual typedList) {
		List<Individual> members = new ArrayList<Individual>();
		Individual rest = typedList;
		while (rest != null) {
			RDFNode first = rest.getPropertyValue(model.getProperty(SadlConstants.SADL_LIST_MODEL_FIRST_URI));
			if (first != null && first.isResource() && first.asResource().canAs(Individual.class)) {
				members.add(first.asResource().as(Individual.class));
				RDFNode restRdfNode = rest.getPropertyValue(model.getProperty(SadlConstants.SADL_LIST_MODEL_REST_URI));
				if (restRdfNode != null && restRdfNode.canAs(Individual.class)) {
					rest = restRdfNode.as(Individual.class);
				}
				else {
					rest = null;
				}
			}
		}
		return members;
	}
	
	@Override
	public String[] extractPythonTFEquationFromCodeExtractionModel(String pythonScript) {
		return extractPythonTFEquationFromCodeExtractionModel(pythonScript, null);
	}

	@Override
	public String[] extractPythonTFEquationFromCodeExtractionModel(String pythonScript, String defaultMethodName) {
		String modifiedScript;		
		if (pythonScript.contains(" math.")) {
			modifiedScript = pythonScript.replaceAll("math.", "tf.math.");
		}
		else {
			modifiedScript = pythonScript.replaceAll("Math.", "tf.math.");
		}
		return extractPythonEquationFromCodeExtractionModel(modifiedScript, defaultMethodName);
	}

	@Override
	public String[] extractPythonEquationFromCodeExtractionModel(String pythonScript) {
		return extractPythonEquationFromCodeExtractionModel(pythonScript, null);
	}

	@Override
	public String[] extractPythonEquationFromCodeExtractionModel(String pythonScript, String defaultMethodName) {
		/*
		 * A typical script coming from the Java to Python service looks like this (indentation preserved):

#!/usr/bin/env python
"""""" generated source for module inputfile """"""
class Mach(object):
    """""" generated source for class Mach """"""
    def CAL_SOS(self, T, G, R, Q):
        """""" generated source for method CAL_SOS """"""
        WOW = 1 + (G - 1) / (1 + (G - 1) * Math.pow((Q / T), 2) * Math.exp(Q / T) / Math.pow((Math.exp(Q / T) - 1), 2))
        return (Math.sqrt(32.174 * T * R * WOW))

		 * We need to find the line containing "generated source for method <methodName>" and extract the name of the method.
		 * Then we need to replace the "return " with "<methodName> =".
		 * If there are multiple rows (newlines) we need to place the number of spaces that the row ending in the newline is indented from the "def <methName>..." line.
		 * This becomes the name of the output variable in K-CHAIN.
		 */
		String methName = null;
		StringBuilder sb = new StringBuilder();
		int indent = -1;
		Scanner scanner = new Scanner(pythonScript);
		String lastLine = "";
		int lineCnt = 0;
		int firstLine = 0;
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String trimmedLine = line.trim();
			if (methName != null) {
				if (lineCnt > firstLine) {
					for (int i = 0; i < indent; i++) {	// provide correct indent
						sb.append(" ");
					}
				}
				int returnIdx = trimmedLine.indexOf("return");
				if (returnIdx >= 0) {
					sb.append(methName);
					sb.append(" = ");
					sb.append(trimmedLine.substring(returnIdx + 7));
				}
				else {
					sb.append(trimmedLine);
				}
			}
			int idx = trimmedLine.indexOf("generated source for method");
			if (idx > 0) {
				methName = trimmedLine.substring(idx + 28);
				for (int i = 0; i < methName.length(); i++) {
					if (methName.charAt(i) == '\"' || Character.isWhitespace(methName.charAt(i))) {
						methName  = methName.substring(0, i);
						break;
					}
				}
				int lastLineIndent = getLineIndent(lastLine);
				int thisLineIndent = getLineIndent(line);
				indent = thisLineIndent - lastLineIndent;
				firstLine = lineCnt + 1;
			}
			lastLine = line;
			if (scanner.hasNextLine() && methName != null && sb.length() > 0) {
				sb.append("\n");
			}
			lineCnt++;
		}
		scanner.close();
	    String modifiedScript = sb.length() > 0 ? sb.toString() : pythonScript;
		String[] returns = new String[2];
		returns[0] = methName != null ? methName : defaultMethodName;
		returns[1] = modifiedScript;
		return returns;
	}
	
	private int getLineIndent(String line) {
		int indent = 0;
		for (int lidx = 0; lidx < line.length(); lidx++) {
			if (Character.isWhitespace(line.charAt(lidx))) {
				indent++;
			}
			else {
				break;
			}
		}
		return indent;
	}

}
