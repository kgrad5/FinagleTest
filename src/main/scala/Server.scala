import com.twitter.finagle.{Http, Service}
import com.twitter.util.{Await, Future}
import java.net.InetSocketAddress
import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.handler.codec.http.DefaultHttpResponse
import org.jboss.netty.handler.codec.http.HttpRequest
import org.jboss.netty.handler.codec.http.HttpResponse
import org.jboss.netty.handler.codec.http.HttpResponseStatus
import org.jboss.netty.handler.codec.http.HttpVersion;
import java.nio.charset.Charset


object Server extends App {
 
  def handler(req: HttpRequest): HttpResponse = {
    req.getUri() match {
    	case "/" => {
          return new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
        }
        case "/hello" => { 
        	val response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
        	response.setContent(ChannelBuffers.copiedBuffer("Hello, World!", Charset.forName("UTF-8")))     
        	response
        }
        case _ => return new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST)
     }
  }
  
  val service = new Service[HttpRequest, HttpResponse] {
    def apply(req: HttpRequest): Future[HttpResponse] = { 
      val response = handler(req)		
      Future.value(response)
    }    
  }
  val server = Http.serve(":8080", service)
  Await.ready(server)
}

