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
 
  def respond(res: String): HttpResponse = {
    val response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
        	response.setContent(ChannelBuffers.copiedBuffer(res, Charset.forName("UTF-8")))     
        	response
  }
  
  def handler(req: HttpRequest): HttpResponse = {
    req.getUri() match {
    	case "/" =>  new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
        case "/hello" => { 
        	val response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
        	respond("Hello, World!")
        }
        case "/list" => {
          val f: java.io.File = new java.io.File(".")
          val files: String = f.listFiles() map ({ x => if (x.isDirectory()) x.getName() + "/" else x.getName()}) reduce (_ + "\n" + _)
          respond(files)
        }
        case _ => new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST)
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

