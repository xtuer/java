import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class InboundHandler2 extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("InboundHandler2.channelRead(): " + ByteBufUtil.hexDump((ByteBuf) msg));
        // System.out.println(ctx);
        // ctx.fireChannelRead(msg);

        // ctx.channel().writeAndFlush(InboundHandler1.RESPONSE.copy());
        // ReferenceCountUtil.release(msg);
    }
}
