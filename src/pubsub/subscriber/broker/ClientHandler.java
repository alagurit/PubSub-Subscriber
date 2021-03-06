/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pubsub.subscriber.broker;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import pubsub.message.NetworkMessage;
import pubsub.subscriber.ui.SubscriberUI;

/**
 *
 * @author AL
 */
class ClientHandler extends SimpleChannelInboundHandler<NetworkMessage.Messages> {

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private static final Pattern DELIM = Pattern.compile("/");
    // Stateful properties
    private volatile Channel channel;
    
    public ClientHandler() {
        super(false);
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext chc, NetworkMessage.Messages msg) throws Exception {
        //Handle the broker-to-subscriber response
        if(msg.getMessageType() == NetworkMessage.Messages.MessageType.GET_TOPICS){
            ArrayList<String> allTopics= new ArrayList<String>();
            for(int i=0;i<msg.getTopicsList().size();i++){
                allTopics.add(msg.getTopicsList().get(i));
            }
            SubscriberUI.allTopics = allTopics;
        }
        if(msg.getMessageType() == NetworkMessage.Messages.MessageType.GET_SUBSCRIBEDTOPICS){
            
            ArrayList<String> subscribedTopics= new ArrayList<String>();
            for(int i=0;i<msg.getTopicsList().size();i++){
                subscribedTopics.add(msg.getTopicsList().get(i));
            }
            SubscriberUI.subscribedTopics = subscribedTopics;
            System.out.println("SubscribedTopics: channelRead0: " + subscribedTopics.size());
        }
        
    }
    
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        channel = ctx.channel();
        System.out.println("hello");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.log(Level.WARNING,"Unexpected exception from downstream.", cause);
        ctx.close();
    }
    
    boolean send(NetworkMessage.Messages msg) {
        
        System.out.println(channel.isOpen() + "-" + channel.isWritable() + channel.remoteAddress());
        ChannelFuture write = channel.writeAndFlush(msg);
        return write.isSuccess();
    }
}
