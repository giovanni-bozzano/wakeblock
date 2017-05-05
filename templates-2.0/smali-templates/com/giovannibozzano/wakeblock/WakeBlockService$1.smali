.class Lcom/giovannibozzano/wakeblock/WakeBlockService$1;
.super Ljava/lang/Object;
.source "WakeBlockService.java"

# interfaces
.implements Landroid/content/ServiceConnection;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/giovannibozzano/wakeblock/WakeBlockService;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;


# direct methods
.method constructor <init>(Lcom/giovannibozzano/wakeblock/WakeBlockService;)V
    .registers 2

    iput-object p1, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$1;->this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onServiceConnected(Landroid/content/ComponentName;Landroid/os/IBinder;)V
    .registers 9

    const/4 v5, 0x0

    iget-object v3, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$1;->this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    new-instance v4, Landroid/os/Messenger;

    invoke-direct {v4, p2}, Landroid/os/Messenger;-><init>(Landroid/os/IBinder;)V

    invoke-static {v3, v4}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->-set1(Lcom/giovannibozzano/wakeblock/WakeBlockService;Landroid/os/Messenger;)Landroid/os/Messenger;

    iget-object v3, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$1;->this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    const/4 v4, 0x1

    invoke-static {v3, v4}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->-set2(Lcom/giovannibozzano/wakeblock/WakeBlockService;Z)Z

    const/4 v3, 0x0

    const/4 v4, 0x3

    :try_start_13
    invoke-static {v3, v4}, Landroid/os/Message;->obtain(Landroid/os/Handler;I)Landroid/os/Message;

    move-result-object v2

    new-instance v0, Landroid/os/Bundle;

    invoke-direct {v0}, Landroid/os/Bundle;-><init>()V

    const-string/jumbo v3, "version"

    const-string/jumbo v4, "1.0"

    invoke-virtual {v0, v3, v4}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    invoke-virtual {v2, v0}, Landroid/os/Message;->setData(Landroid/os/Bundle;)V

    iget-object v3, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$1;->this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    invoke-static {v3}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->-get1(Lcom/giovannibozzano/wakeblock/WakeBlockService;)Landroid/os/Messenger;

    move-result-object v3

    invoke-virtual {v3, v2}, Landroid/os/Messenger;->send(Landroid/os/Message;)V
    :try_end_31
    .catch Landroid/os/RemoteException; {:try_start_13 .. :try_end_31} :catch_32

    :goto_31
    return-void

    :catch_32
    move-exception v1

    iget-object v3, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$1;->this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    invoke-static {v3, v5}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->-set1(Lcom/giovannibozzano/wakeblock/WakeBlockService;Landroid/os/Messenger;)Landroid/os/Messenger;

    iget-object v3, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$1;->this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    const/4 v4, 0x0

    invoke-static {v3, v4}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->-set2(Lcom/giovannibozzano/wakeblock/WakeBlockService;Z)Z

    goto :goto_31
.end method

.method public onServiceDisconnected(Landroid/content/ComponentName;)V
    .registers 4

    iget-object v0, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$1;->this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    const/4 v1, 0x0

    invoke-static {v0, v1}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->-set1(Lcom/giovannibozzano/wakeblock/WakeBlockService;Landroid/os/Messenger;)Landroid/os/Messenger;

    iget-object v0, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$1;->this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    const/4 v1, 0x0

    invoke-static {v0, v1}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->-set2(Lcom/giovannibozzano/wakeblock/WakeBlockService;Z)Z

    invoke-static {}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->-get0()Ljava/lang/Object;

    move-result-object v1

    monitor-enter v1

    :try_start_11
    invoke-static {}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->-get0()Ljava/lang/Object;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/Object;->notifyAll()V
    :try_end_18
    .catchall {:try_start_11 .. :try_end_18} :catchall_1a

    monitor-exit v1

    return-void

    :catchall_1a
    move-exception v0

    monitor-exit v1

    throw v0
.end method
