# nem2-sdk-openapi-gen
Project in charge of generating the nem2-sdk clients using openapi and gradle.

The gradle script will create and install different flavours of java rest clients according to the current NEM Open API specification.

The official NEM Api descriptor can be found here:

https://github.com/nemtech/nem2-docs/blob/master/source/resources/collections/openapi3.yaml

# Requirements

* Java 8 - One way of installing is using [sdkman](https://sdkman.io/)

# How to use it

Once tools have been installed, just run

`` ../gradlew clean generate ``
`` ../gradlew install ``

The script will:

1. Generate one lib per flavor/framework/library. Currently vertx, jersey2 and okhttp-gson (see possible frameworks here https://openapi-generator.tech/docs/generators/java)
2. Build each library
3. Install the libraries locally using gradle

# Notes:

* Running the generator is not required to build [nem2-sdk-java](https://github.com/nemtech/nem2-sdk-java) . The generated libs are published into the maven central repository.  The nem2-sdk-java depends on those libraries like any other third party dependency.
* The generated lib version (artifact version) should be consistent with the openapi3 YAML. Current version is 0.7.19 so the generated libraries should have the version 0.7.19. If the descriptor changes and the version is updated, the libraries should be upgraded, regenerated and deployed.
* The generator uses a patched version of the descriptor due to the AnyOf open [API bug](https://github.com/OpenAPITools/openapi-generator/issues/634)
* There is a small tune in the generation that uses BigInteger attributes instead of String when the field is a String number. The tune is by using ``typeMappings =  ["x-number-string": "java.math.BigInteger"]`` and by replacing the string type to ``x-number-string`` in the ``openapi3-any-of-patch.yaml``.
* Generated code must not be changed not committed!!! target folder is gitignored. If there is something wrong with the generated code, you need to [customize the generator](https://openapi-generator.tech/docs/customization.html)

# TODOs:

* Move this folder to it's own github repository.
* Read version from the apenapi3 YAML file. At the moment the version needs to be copied and pasted from the descriptor to the script.
* User real YAML from the web once the any of generation and the string number customization is fixed
