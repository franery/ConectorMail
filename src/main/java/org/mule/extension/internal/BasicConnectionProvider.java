package org.mule.extension.internal;

import org.mule.runtime.api.connection.CachedConnectionProvider;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.connection.PoolingConnectionProvider;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Password;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * This class (as it's name implies) provides connection instances and the funcionality to disconnect and validate those
 * connections.
 * <p>
 * All connection related parameters (values required in order to create a connection) must be
 * declared in the connection providers.
 * <p>
 * This particular example is a {@link PoolingConnectionProvider} which declares that connections resolved by this provider
 * will be pooled and reused. There are other implementations like {@link CachedConnectionProvider} which lazily creates and
 * caches connections or simply {@link ConnectionProvider} if you want a new connection each time something requires one.
 */
public class BasicConnectionProvider implements PoolingConnectionProvider<BasicConnection> {

	@Parameter
	protected String host;

	/**
	 * Username used to connect with the mail server.
	 */
	@Parameter
	@Optional
	protected String user;

	/**
	 * Username password to connect with the mail server.
	 */
	@Parameter
	@Password
	@Optional
	protected String password;

	@Parameter
	@Optional(defaultValue = "993")
	@Placement(order = 2)
	@Summary("The port number of the mail server. '993' by default.")
	private String port;

	@Override
	public BasicConnection connect() throws ConnectionException {
		return new BasicConnection(user,password,host, port, null);
	}

	@Override
	public void disconnect(BasicConnection connection) {

	}

	@Override
	public ConnectionValidationResult validate(BasicConnection connection) {
		return ConnectionValidationResult.success();
	}
}
