package com.example.rstaak.service

import io.socket.emitter.Emitter


class SocketEventListener(private val mEvent: String, private val mListener: Listener?) :
    Emitter(), Emitter.Listener
{
    override fun call(vararg objects: Any?)
    {
        mListener?.onEventCall(mEvent, *objects)
    }

    interface Listener
    {
        fun onEventCall(event: String?, vararg objects: Any?)
    }
}