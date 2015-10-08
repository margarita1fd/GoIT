package YouTrack;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Cookies;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.*;
import static com.jayway.restassured.matcher.RestAssuredMatchers.*;
//import static com.jayway.restassured.response.Response.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class IssueMethods {
    Cookies cookies;

    @Before
    public void setUp() throws Exception {
        RestAssured.baseURI = "http://goit2.myjetbrains.com/youtrack/rest/";

        Response response = given().
                param("login", "margarita1fd").
                param("password", "123456").

                when().
                post("user/login");
        cookies = response.getDetailedCookies();

    }


    @Test
    public void testCreateIssue() throws Exception {

        given().
                cookies(cookies).
                param("project", "GOITQA").
                param("summary", "My summary").
                param("description", "My description").

                when().
                put("/issue").

                then().statusCode(201);
    }


    private String createTestIssue() throws Exception {

        Response response = given().
                cookies(cookies).
                param("project", "GOITQA").
                param("summary", "My summary").
                param("description", "My description").

                when().
                put("/issue");

        String location = response.getHeader("Location");
        String issueId = location.substring(location.lastIndexOf("/") + 1);
        return issueId;

    }


    @Test
    public void testDeleteIssue() throws Exception {

        String issueID = createTestIssue();

        given().
                cookies(cookies).

                when().
                delete("/issue/" + issueID).

                then().
                statusCode(200);
    }


    @Test
    public void testGetIssue() throws Exception {

        String issueID = createTestIssue();

        given().
                cookies(cookies).
                when().
                get("/issue/" + issueID).
                then().
                statusCode(200).
                body("issue.@id", equalTo(issueID)).
                body("issue.field.find {it.@name == 'summary'}.value", equalTo("My summary")).
                body("issue.field.find {it.@name == 'description'}.value", equalTo("My description")).
                extract().response();
        //System.out.print(response.asString());
    }

    @Test

    public void testIssueExist() throws Exception {
        String issueID = createTestIssue();
        given().
                cookies(cookies).
                when().
                get("/issue/" + issueID + "/exists").
                then().statusCode(200);

    }

    @Test
    public void testIssueNotExist() throws Exception {
        String issueID = "GOITQA2";
        given().
                cookies(cookies).
                when().
                get("/issue/" + issueID + "/exists").
                then().statusCode(404);

    }

    @Test
    public void testGetIssueInProject() throws Exception {

        String projectID = "GOITQA";
        //String filter = "My summary";
        Response r = (Response) given().
                cookies(cookies).
                param("project", "GOITQA").
                param("filter", "").
                param("after", "70").
                param("max", "5").
                param("updatedAfter").

                when().
                get("/issue/byproject/" + projectID);

                //then().
               // statusCode(200);

        System.out.print(r.asString());

    }

    @Test
    public void testGetNumberOfIssues() throws Exception {

        Response response = given().
                cookies(cookies).
                param("filter", "").
                param("callback", "").

                when().
                get("/issue/count");

        int issuesNumber = Integer.parseInt(response.asString().replaceAll("[\\D]", ""));
        System.out.println(issuesNumber);

        assertThat(issuesNumber, greaterThanOrEqualTo(100));
        //assertThat(issuesNumber, greaterThan(5));
    }

    @Test
    public void testGetAllProjects() throws Exception {

        Response response = given().
                cookies(cookies).
                param("verbose", "true").
                when().
                get("/project/all/");

        System.out.print(response.asString());
    }

    @Test
    public void testUpdateAnIssueById() throws Exception {
        String issueID = createTestIssue();

        given().
                cookies(cookies).
                param("project", "GOITQA").
                param("summary", "My updated summary").
                param("description", "My updated description").

                when().
                post("/issue/"+issueID).

                then().
                statusCode(200);

        System.out.println("Issue "+ issueID + "has successfully created and updated.");
    }

    @Test
    public void testUpdateAnIssueNotExist() throws Exception {

        given().
                cookies(cookies).
                param("project", "GOITQA").
                param("summary", "My updated summary").
                param("description", "My updated description").

                when().
                post("/issue/letsfun").

                then().
                statusCode(404);
    }

    @Test
    public void testGetInfoForCurrentUser() throws Exception {

        Response currentUser = given().
                cookies(cookies).
                when().
                get("/user/current");

        System.out.println(currentUser.asString());

    }

    @Test
    public void testGetUserByLogin() throws Exception {

        String user = "root";
        //String user1 = "Mariia";
        Response userByLogin = given().
                cookies(cookies).
               // param("user", "").
                when().
                get("/user/" + user);

        System.out.println(userByLogin.asString());
    }
}

