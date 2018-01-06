.class Lcom/giovannibozzano/wakeblock/WakeBlockService$2;
.super Landroid/os/Handler;
.source "WakeBlockService.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/giovannibozzano/wakeblock/WakeBlockService;-><init>()V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;


# direct methods
.method constructor <init>(Lcom/giovannibozzano/wakeblock/WakeBlockService;Landroid/os/Looper;)V
    .registers 3

    iput-object p1, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$2;->this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    invoke-direct {p0, p2}, Landroid/os/Handler;-><init>(Landroid/os/Looper;)V

    return-void
.end method


# virtual methods
.method public handleMessage(Landroid/os/Message;)V
    .registers 9

    const/4 v6, 0x0

    const/4 v5, 0x0

    iget v3, p1, Landroid/os/Message;->what:I

    packed-switch v3, :pswitch_data_5c

    invoke-super {p0, p1}, Landroid/os/Handler;->handleMessage(Landroid/os/Message;)V

    :goto_a
    return-void

    :pswitch_b
    invoke-static {}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->-get0()Ljava/lang/Object;

    move-result-object v4

    monitor-enter v4

    :try_start_10
    invoke-static {}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->-get0()Ljava/lang/Object;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/Object;->notify()V
    :try_end_17
    .catchall {:try_start_10 .. :try_end_17} :catchall_19

    :goto_17
    monitor-exit v4

    goto :goto_a

    :catchall_19
    move-exception v3

    monitor-exit v4

    throw v3

    :pswitch_1c
    invoke-static {}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->-get0()Ljava/lang/Object;

    move-result-object v4

    monitor-enter v4

    const/4 v3, 0x0

    :try_start_22
    invoke-static {v3}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->-set0(Z)Z

    invoke-static {}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->-get0()Ljava/lang/Object;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/Object;->notify()V
    :try_end_2c
    .catchall {:try_start_22 .. :try_end_2c} :catchall_2d

    goto :goto_17

    :catchall_2d
    move-exception v3

    monitor-exit v4

    throw v3

    :pswitch_30
    const/4 v3, 0x0

    const/4 v4, 0x3

    :try_start_32
    invoke-static {v3, v4}, Landroid/os/Message;->obtain(Landroid/os/Handler;I)Landroid/os/Message;

    move-result-object v2

    new-instance v0, Landroid/os/Bundle;

    invoke-direct {v0}, Landroid/os/Bundle;-><init>()V

    const-string/jumbo v3, "version"

    const/4 v4, 0x0

    invoke-virtual {v0, v3, v4}, Landroid/os/Bundle;->putShort(Ljava/lang/String;S)V

    invoke-virtual {v2, v0}, Landroid/os/Message;->setData(Landroid/os/Bundle;)V

    iget-object v3, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$2;->this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    invoke-static {v3}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->-get1(Lcom/giovannibozzano/wakeblock/WakeBlockService;)Landroid/os/Messenger;

    move-result-object v3

    invoke-virtual {v3, v2}, Landroid/os/Messenger;->send(Landroid/os/Message;)V
    :try_end_4e
    .catch Landroid/os/RemoteException; {:try_start_32 .. :try_end_4e} :catch_4f

    goto :goto_a

    :catch_4f
    move-exception v1

    iget-object v3, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$2;->this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    invoke-static {v3, v6}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->-set1(Lcom/giovannibozzano/wakeblock/WakeBlockService;Landroid/os/Messenger;)Landroid/os/Messenger;

    iget-object v3, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$2;->this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    invoke-static {v3, v5}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->-set2(Lcom/giovannibozzano/wakeblock/WakeBlockService;Z)Z

    goto :goto_a

    nop

    :pswitch_data_5c
    .packed-switch 0x0
        :pswitch_b
        :pswitch_1c
        :pswitch_30
    .end packed-switch
.end method
