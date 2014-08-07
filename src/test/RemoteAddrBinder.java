package test;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import javax.inject.Inject;
import javax.inject.Provider;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.internal.PropertiesDelegate;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ContainerRequest;

public class RemoteAddrBinder extends AbstractBinder {

    private static class RemoteAddrProviderFactory implements Factory<SocketAddress> {

        @Inject
        private Provider<ContainerRequest> request;

        @Override
        public SocketAddress provide() {
            ContainerRequest containerRequest = request.get();
            PropertiesDelegate delegate = containerRequest.getPropertiesDelegate();
            try {
                Field requestField = delegate.getClass().getDeclaredField("request");
                requestField.setAccessible(true);
                Request grizzlyRequest = (Request) requestField.get(delegate);
                return new InetSocketAddress(grizzlyRequest.getRemoteAddr(), grizzlyRequest.getRemotePort());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void dispose(SocketAddress instance) {
        }
    }

    @Override
    protected void configure() {
        bindFactory(RemoteAddrProviderFactory.class).to(SocketAddress.class).in(RequestScoped.class);
    }
}
