## Models From Text ##

**The following installation instructions are for a Linux environment:**

1. Build and run the docker container for searching over Wikidata terms. This docker container will load Wikidata terms from [ModelsFromText/wikidata-for-elasticsearch](https://github.com/GEGlobalResearch/DARPA-ASKE-TA1/blob/development_text_extraction/ModelsFromText/wikidata-for-elasticsearch/all-terms-wikidata.json) into an Elasticsearch instance.

2. Download and run an docker container consisting of an ontology-driven concept mapper service.

3. Create a conda environment for text extraction services: `conda env create -f conda-env/aske-ta1-text-env.yml`

4. Run `source activate aske-ta1` to check if the environment is created.

5. Run `./getTextExtractionModels.sh` to download pre-trained model for extraction of scientific concepts and equations from text. This will create a resources folder under `ModelsFromText`.

5. Running `./startServicesModelsFromText.sh` to start all the relevant text extraction services.

6. The services will be running if you are able to access its [API documentation](http://localhost:4200/darpa/aske/ui/)

7. Log files for the services can be found under `logs/`.

8. Start a jupyter notebook `jupyter notebook`.

9. Example usage for text-extraction APIs and overview in each service can be found in the following notebook `api-example-code/text_to_triples_client.ipynb` 

(Tip: If you are behind a firewall, make sure your proxies are set correctly)