package Event;

/**
 * Created by S.R Rain on 1/13/2016.
 */
public interface IHttpResponse {

    public  void RequestSuccess(Object response);
    public  void RequestFailed(String response);
}
