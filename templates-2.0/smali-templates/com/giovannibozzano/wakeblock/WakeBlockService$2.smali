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
    .registers 4

    iget v0, p1, Landroid/os/Message;->what:I

    packed-switch v0, :pswitch_data_2e

    invoke-super {p0, p1}, Landroid/os/Handler;->handleMessage(Landroid/os/Message;)V

    :goto_8
    return-void

    :pswitch_9
    invoke-static {}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->-get0()Ljava/lang/Object;

    move-result-object v1

    monitor-enter v1

    :try_start_e
    invoke-static {}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->-get0()Ljava/lang/Object;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/Object;->notify()V
    :try_end_15
    .catchall {:try_start_e .. :try_end_15} :catchall_17

    :goto_15
    monitor-exit v1

    goto :goto_8

    :catchall_17
    move-exception v0

    monitor-exit v1

    throw v0

    :pswitch_1a
    invoke-static {}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->-get0()Ljava/lang/Object;

    move-result-object v1

    monitor-enter v1

    const/4 v0, 0x0

    :try_start_20
    invoke-static {v0}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->-set0(Z)Z

    invoke-static {}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->-get0()Ljava/lang/Object;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/Object;->notify()V
    :try_end_2a
    .catchall {:try_start_20 .. :try_end_2a} :catchall_2b

    goto :goto_15

    :catchall_2b
    move-exception v0

    monitor-exit v1

    throw v0

    :pswitch_data_2e
    .packed-switch 0x0
        :pswitch_9
        :pswitch_1a
    .end packed-switch
.end method
