import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class InboundHandler1 extends ChannelInboundHandlerAdapter {
    public static final ByteBuf RESPONSE = Unpooled.copiedBuffer("H".getBytes());

    // 有连接建立成功
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("++++InboundHandler1.channelActive(): " + ctx.channel().remoteAddress());
        ctx.pipeline().writeAndFlush(RESPONSE.copy());
    }

    int count = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 重要: 每次 channelRead 调用后，即使不读取 msg，下次 channelRead 调用时前面未读取的数据也会没了，
        //      所以需要自己缓存起来，参考 ByteToMessageDecoder

        System.out.println("====>");
        // System.out.println("InboundHandler1.channelRead(): " + ByteBufUtil.hexDump((ByteBuf) msg));

        if (count++ > 4) {
            System.out.println("InboundHandler1.channelRead(): " + ByteBufUtil.hexDump((ByteBuf) msg));
        }
        //
        // ctx.alloc().buffer();

        // System.out.println(ctx);

        // [1] 传递给下一个 InboundHandler
        // ctx.fireChannelRead(msg);

        // [2] 直接给 Transport，不通过 pipeline (即不通过 OutboundHandler)，没传递到下一个 InboundHandler
        // ctx.writeAndFlush(RESPONSE.copy());

        // [3] 传递个第一个 OutboundHandler，通过 pipeline
        // ctx.channel().writeAndFlush(RESPONSE.copy());
        // ctx.pipeline().writeAndFlush(RESPONSE.copy());

        // ReferenceCountUtil.release(msg); // [*] 如果 msg 不使用，且不传递给下一个 handler，释放它
    }
}
