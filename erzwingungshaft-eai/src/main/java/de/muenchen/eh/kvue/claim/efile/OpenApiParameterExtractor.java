package de.muenchen.eh.kvue.claim.efile;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.parser.OpenAPIV3Parser;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Extracts parameters from an OpenAPI specification for a given operationId.
 */
public class OpenApiParameterExtractor {

    private final OpenAPI openAPI;

    /**
     * Initializes the extractor by parsing the OpenAPI specification from the given path.
     *
     * @param openApiJsonPath Path to the OpenAPI JSON file (e.g.,
     *            "classpath:openapi/dmsresteai-openapi.json").
     */
    public OpenApiParameterExtractor(String openApiJsonPath) {
        this.openAPI = new OpenAPIV3Parser().read(openApiJsonPath);
    }

    /**
     * Retrieves all parameters for a given operationId.
     *
     * @param operationId The operationId as defined in the OpenAPI specification.
     * @return List of Parameter objects, or an empty list if the operationId is not found.
     */
    public List<Parameter> getParametersForOperation(String operationId) {
        return openAPI.getPaths().values().stream()
                .flatMap(pathItem -> pathItem.readOperations().stream())
                .filter(operation -> operationId.equals(operation.getOperationId()))
                .findFirst()
                .map(Operation::getParameters)
                .orElse(List.of());
    }

    /**
     * Retrieves the names of all parameters for a given operationId.
     *
     * @param operationId The operationId as defined in the OpenAPI specification.
     * @return List of parameter names, or an empty list if the operationId is not found.
     */
    public List<String> getParameterNamesForOperation(String operationId) {
        return getParametersForOperation(operationId).stream()
                .map(Parameter::getName)
                .collect(Collectors.toList());
    }
}
