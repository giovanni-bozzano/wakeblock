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
    .registers 8

    iget-object v0, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$1;->this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    new-instance v1, Landroid/os/Messenger;

    invoke-direct {v1, p2}, Landroid/os/Messenger;-><init>(Landroid/os/IBinder;)V

    # setter for: Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;
    invoke-static {v0, v1}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->access$002(Lcom/giovannibozzano/wakeblock/WakeBlockService;Landroid/os/Messenger;)Landroid/os/Messenger;

    iget-object v0, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$1;->this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    const/4 v1, 0x1

    # setter for: Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceBound:Z
    invoke-static {v0, v1}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->access$102(Lcom/giovannibozzano/wakeblock/WakeBlockService;Z)Z

    const/4 v0, 0x3

    const/4 v2, 0x0

    :try_start_12
    invoke-static {v2, v0}, Landroid/os/Message;->obtain(Landroid/os/Handler;I)Landroid/os/Message;

    move-result-object v0

    new-instance v3, Landroid/os/Bundle;

    invoke-direct {v3}, Landroid/os/Bundle;-><init>()V

    const-string v4, "version"

    invoke-virtual {v3, v4, v1}, Landroid/os/Bundle;->putShort(Ljava/lang/String;S)V

    invoke-virtual {v0, v3}, Landroid/os/Message;->setData(Landroid/os/Bundle;)V

    iget-object v1, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$1;->this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    # getter for: Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;
    invoke-static {v1}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->access$000(Lcom/giovannibozzano/wakeblock/WakeBlockService;)Landroid/os/Messenger;

    move-result-object v1

    invoke-virtual {v1, v0}, Landroid/os/Messenger;->send(Landroid/os/Message;)V
    :try_end_2c
    .catch Landroid/os/RemoteException; {:try_start_12 .. :try_end_2c} :catch_2d

    goto :goto_39

    :catch_2d
    move-exception v0

    iget-object v1, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$1;->this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    # setter for: Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;
    invoke-static {v1, v2}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->access$002(Lcom/giovannibozzano/wakeblock/WakeBlockService;Landroid/os/Messenger;)Landroid/os/Messenger;

    iget-object v1, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$1;->this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    const/4 v2, 0x0

    # setter for: Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceBound:Z
    invoke-static {v1, v2}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->access$102(Lcom/giovannibozzano/wakeblock/WakeBlockService;Z)Z

    :goto_39
    return-void
.end method

.method public onServiceDisconnected(Landroid/content/ComponentName;)V
    .registers 4

    iget-object v0, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$1;->this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    const/4 v1, 0x0

    # setter for: Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;
    invoke-static {v0, v1}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->access$002(Lcom/giovannibozzano/wakeblock/WakeBlockService;Landroid/os/Messenger;)Landroid/os/Messenger;

    iget-object v0, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$1;->this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    const/4 v1, 0x0

    # setter for: Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceBound:Z
    invoke-static {v0, v1}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->access$102(Lcom/giovannibozzano/wakeblock/WakeBlockService;Z)Z

    # getter for: Lcom/giovannibozzano/wakeblock/WakeBlockService;->lock:Ljava/lang/Object;
    invoke-static {}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->access$200()Ljava/lang/Object;

    move-result-object v0

    monitor-enter v0

    :try_start_11
    # getter for: Lcom/giovannibozzano/wakeblock/WakeBlockService;->lock:Ljava/lang/Object;
    invoke-static {}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->access$200()Ljava/lang/Object;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/Object;->notifyAll()V

    monitor-exit v0

    return-void

    :catchall_1a
    move-exception v1

    monitor-exit v0
    :try_end_1c
    .catchall {:try_start_11 .. :try_end_1c} :catchall_1a

    throw v1
.end method
