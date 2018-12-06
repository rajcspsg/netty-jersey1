package ads.netty.jersey.resources;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "account")
public class CombinedAnnotationBean {

    @JsonProperty("value")
    int x;

    public CombinedAnnotationBean(int x) {
        this.x = x;
    }

    public CombinedAnnotationBean() {
        this(15);
    }
}
