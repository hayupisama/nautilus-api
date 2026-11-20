package fr.nautilus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

/** Demarrage complet : Dev Services PostgreSQL/PostGIS, migrations Flyway, sante, metriques. */
@QuarkusTest
class HealthEndpointTest {

    @Inject
    DataSource dataSource;

    @Test
    void healthIsUp() {
        given().when().get("/q/health").then().statusCode(200).body("status", is("UP"));
    }

    @Test
    void managementEndpointsStayOutsideApiPrefix() {
        given().when().get("/v1/q/health").then().statusCode(404);
    }

    @Test
    void flywayAppliedMigrationsAndPostgisIsAvailable() throws Exception {
        try (var connection = dataSource.getConnection();
                var statement = connection.createStatement();
                var result = statement.executeQuery("SELECT PostGIS_Version()")) {
            org.junit.jupiter.api.Assertions.assertTrue(result.next());
        }
    }

    @Test
    void prometheusMetricsAreExposed() {
        given().when().get("/q/metrics").then().statusCode(200).body(containsString("jvm_memory"));
    }

    @Test
    void openApiDocumentIsExposed() {
        given().when().get("/q/openapi").then().statusCode(200);
    }
}
