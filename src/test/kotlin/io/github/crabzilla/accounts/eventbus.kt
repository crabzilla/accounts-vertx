package io.github.crabzilla.accounts

import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageCodec
import io.vertx.core.eventbus.impl.codecs.ClusterSerializableCodec
import io.vertx.core.shareddata.ClusterSerializable

//class JacksonCodec<T>(private val mapper: ObjectMapper, private val clazz: Class<T>) : MessageCodec<T, T> {
//  override fun encodeToWire(buffer: Buffer, s: T) {
//    buffer.appendBytes(mapper.writeValueAsBytes(s))
//  }
//
//  override fun decodeFromWire(pos: Int, buffer: Buffer): T {
//    val length = buffer.getInt(pos)
//    val bytes = buffer.getBytes(pos + 4, pos + 4 + length)
//    return mapper.readValue(bytes, clazz)
//  }
//}


class x : ClusterSerializable {

  override fun writeToBuffer(p0: Buffer?) {
    TODO("Not yet implemented")
  }

  override fun readFromBuffer(p0: Int, p1: Buffer?): Int {
    TODO("Not yet implemented")
  }

}