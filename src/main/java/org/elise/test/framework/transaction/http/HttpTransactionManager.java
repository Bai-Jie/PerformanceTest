//package org.elise.test.framework.transaction.http;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.config.RequestConfig;
//import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
//import org.apache.http.concurrent.FutureCallback;
//
//import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
//import org.apache.http.impl.nio.client.HttpAsyncClients;
//import org.apache.http.util.EntityUtils;
//import org.elise.test.exception.InvalidRequestException;
//import org.elise.test.exception.NullRequestException;
//import org.elise.test.framework.transaction.TransactionManager;
//import org.elise.test.lr.LrTransStatusManager;
//import org.elise.test.tracer.Tracer;
//
//import java.io.IOException;
//
///**
// * Created by huxuehan on 2016/10/18.
// */
//public class HttpTransactionManager extends TransactionManager {
//    private static RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(3000).setConnectTimeout(3000).build();
//    private static CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom().setDefaultRequestConfig(requestConfig).build();
//    private static Tracer tracer = Tracer.getInstance(HttpTransactionManager.class);
//    private boolean isRunnable;
//
//    public HttpTransactionManager() {
//
//    }
//
//    /**
//     * DomainName shouldn't contain "/" in the end
//     */
//    public HttpTransaction createHttpTransaction(String method, String domainName, Integer remotePort, String remotePath, String transactionName) {
//        return new HttpTransaction(method, domainName, remotePort, remotePath, transactionName, this);
//    }
//
//    @Override
//    public void start() {
//        httpclient.start();
//        isRunnable = true;
//    }
//
//    @Override
//    public void stop() throws IOException {
//        httpclient.close();
//        isRunnable = false;
//    }
//
//    protected void sendTransaction(HttpTransaction trans) throws NullRequestException, InvalidRequestException {
//        if (isRunnable) {
//            sendHttpRequest(trans.getRequest(), trans.getTransactionName(), trans.getCallback());
//            if (trans.getNextTransaction() != null) {
//                try {
//                    Thread.sleep(trans.getIntervalTimeStamp());
//                } catch (InterruptedException e) {
//                    tracer.writeError("Sleeping has been interrupted", e);
//                }
//                sendTransaction((HttpTransaction) trans.getNextTransaction());
//            }
//        }
//    }
//
//    private void sendHttpRequest(HttpEntityEnclosingRequestBase request, final String transactionName, final HttpResultCallBack callBack) {
//            final long begin = System.currentTimeMillis();
//            tracer.writeInfo(request.toString());
//            httpclient.execute(request, new FutureCallback<HttpResponse>() {
//                @Override
//                public void completed(HttpResponse httpResponse) {
//                    if (tracer.isInfoAvailable()) {
//                        tracer.writeInfo(httpResponse.toString());
//                    }
//                    byte[] content = null;
//                    HttpEntity entity = httpResponse.getEntity();
//                    try {
//                        content = EntityUtils.toByteArray(entity);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    if (httpResponse.getStatusLine().getStatusCode() >= 200 && httpResponse.getStatusLine().getStatusCode() < 300) {
//                        LrTransStatusManager.addStatus(transactionName, ((double) System.currentTimeMillis() - (double) begin) / 1000.0, true);
//                        if (callBack != null) {
//                            callBack.success(new String(content));
//                        }
//                    } else {
//                        LrTransStatusManager.addStatus(transactionName, ((double) System.currentTimeMillis() - (double) begin) / 1000.0, false);
//                        if (callBack != null) {
//                            callBack.error(new String(content), httpResponse.getStatusLine().getStatusCode());
//                        }
//                    }
//                }
//
//                @Override
//                public void failed(Exception e) {
//                    LrTransStatusManager.addStatus(transactionName, ((double) System.currentTimeMillis() - (double) begin) / 1000.0, false);
//                    if (callBack != null) {
//                        callBack.failed(e);
//                    }
//                }
//
//                @Override
//                public void cancelled() {
//                    if (callBack != null) {
//                        callBack.unreachable();
//                    }
//                }
//            });
//    }
//}