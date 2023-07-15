# Mini Framework

Minimal framework for testing stuff. Not intended for any serious use.

## HTTP Server example

### Minimal Config

```java
@Configuration
public class Main {


    record User(String name, int age) {}

    @Provides
    HTTPServer server() {
        Router router = new Router(new RequestFactory());
        router.registerRoute(parse("/user/:lastname"), new RequestHandler<String, User>() {

            @Override
            public Response<User> handle(Request<String> request) {
                String lastname = request.pathVariables().get(0);
                return Response.ok(new User("John " + lastname, 123));
            }

            @Override
            public Class<String> requestType() {
                return String.class;
            }
        });
        return new HTTPServer(router);
    }

    public static void main(String[] args) {
        ApplicationContext.fromConfiguration(Main.class)
                .getInstance(HTTPServer.class)
                .start();
    }
}
```

output

```shell
edin Code/mini-framework curl -v http://localhost:8080/user/Doe |jq
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
  0     0    0     0    0     0      0      0 --:--:-- --:--:-- --:--:--     0*   Trying 127.0.0.1:8080...
* Connected to localhost (127.0.0.1) port 8080 (#0)
> GET /user/Doe HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.88.1
> Accept: */*
> 
< HTTP/1.1 200 OK
< Date: Sat, 08 Jul 2023 20:28:15 GMT
< Content-length: 29
< 
{ [29 bytes data]
100    29  100    29    0     0    376      0 --:--:-- --:--:-- --:--:--   408
* Connection #0 to host localhost left intact
{
  "name": "John Doe",
  "age": 123
}
```

Simple Bench for Echo Server

```shell
Server Hostname:        localhost
Server Port:            8080

Document Path:          /hello
Document Length:        36 bytes

Concurrency Level:      50
Time taken for tests:   1.540 seconds
Complete requests:      200000
Failed requests:        0
Keep-Alive requests:    200000
Total transferred:      33800000 bytes
Total body sent:        38400000
HTML transferred:       7200000 bytes
Requests per second:    129836.32 [#/sec] (mean)
Time per request:       0.385 [ms] (mean)
Time per request:       0.008 [ms] (mean, across all concurrent requests)
Transfer rate:          21428.06 [Kbytes/sec] received
                        24344.31 kb/s sent
                        45772.38 kb/s total

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   0.0      0       1
Processing:     0    0   0.1      0       2
Waiting:        0    0   0.1      0       2
Total:          0    0   0.1      0       2

Percentage of the requests served within a certain time (ms)
  50%      0
  66%      0
  75%      0
  80%      0
  90%      1
  95%      1
  98%      1
  99%      1
 100%      2 (longest request)
```

