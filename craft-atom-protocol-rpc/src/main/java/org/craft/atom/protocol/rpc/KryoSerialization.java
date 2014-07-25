package org.craft.atom.protocol.rpc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.craft.atom.protocol.rpc.model.RpcBody;
import org.craft.atom.protocol.rpc.spi.Serialization;
import org.craft.atom.util.Assert;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * The implementor using <a href="https://github.com/EsotericSoftware/kryo">kryo</a>.
 * <p>
 * Not thread safe.
 * 
 * @author mindwind
 * @version 1.0, Jul 23, 2014
 */
public class KryoSerialization implements Serialization<RpcBody> {
	
	// singleton
	private static final KryoSerialization INSTNACE = new KryoSerialization();
	public static KryoSerialization getInstance() { return INSTNACE; } 
	private KryoSerialization() {}
	
	
	// thread local cache
    private static final ThreadLocal<Kryo> CACHE = new ThreadLocal<Kryo>() {
    	@Override
    	protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            kryo.register(RpcBody.class);
            return kryo;
        }
    };
	
	@Override
	public byte type() {
		return 1;
	}

	@Override
	public byte[] serialize(RpcBody rb) {
		Assert.notNull(rb);
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    Output output = new Output(baos);
	    CACHE.get().writeObject(output, rb);
	    output.close();
	    return baos.toByteArray();
	}

	@Override
	public RpcBody deserialize(byte[] bytes) {
		return deserialize(bytes, 0);
	}

	@Override
	public RpcBody deserialize(byte[] bytes, int off) {
		Assert.notNull(bytes);
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes, off, bytes.length - off);
		Input input = new Input(bais);
		RpcBody rb = CACHE.get().readObject(input, RpcBody.class);
	    input.close();
		return rb;
	}

}