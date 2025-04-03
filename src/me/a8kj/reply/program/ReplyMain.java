package me.a8kj.reply.program;

public class ReplyMain {

    private static ReplyProgram replyProgram;

    public static void main(String[] args) {
        replyProgram = new ReplyProgram();
        replyProgram.onEnable();
    }
}
