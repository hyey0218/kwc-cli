package konantech.kwc.cli.common;

import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.RequestOptions;

public class Constants {
	public static final String PROC_PACKAGE = "konantech.kwc.cli.proc.impl.";
	public static final String DEFAULT_PACKAGE = "konantech.kwc.cli.proc.";
	public static final RequestOptions COMMON_OPTIONS;
	static {
	    RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
//	    builder.addHeader("Authorization", "Bearer "); 
//	    builder.addHeader("content-type", "application/json; charset=UTF-8");
//	    builder.setHttpAsyncResponseConsumerFactory(           
//	        new HttpAsyncResponseConsumerFactory
//	            .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
	    COMMON_OPTIONS = builder.build();
	}
}
