package ads.netty.jersey.resources;

import ads.netty.jersey.Student;
import com.technorati.openrtb.model.OpenrtbSchemaBidRequestV25;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/hellonetty")
public class HelloNettyEndpoint {

    @GET
    @Produces("text/plain")
    public String helloNetty() {
        return "Hello Netty";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("text/plain")
    @Path("/student")
    public String getResponse(Student req) {
        System.out.println("request is \n "+ req);
        return "Hello Netty";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("text/plain")
    @Path("/student/text")
    public String getText(Student req) {
        System.out.println("request is  "+ req);
        return "Hello Netty";
    }

}