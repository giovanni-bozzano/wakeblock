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
    .registers 8

    iget v0, p1, Landroid/os/Message;->what:I

    if-eqz v0, :cond_4c

    const/4 v1, 0x0

    const/4 v2, 0x1

    if-eq v0, v2, :cond_38

    const/4 v3, 0x2

    if-eq v0, v3, :cond_f

    invoke-super {p0, p1}, Landroid/os/Handler;->handleMessage(Landroid/os/Message;)V

    goto :goto_5a

    :cond_f
    const/4 v0, 0x3

    const/4 v3, 0x0

    :try_start_11
    invoke-static {v3, v0}, Landroid/os/Message;->obtain(Landroid/os/Handler;I)Landroid/os/Message;

    move-result-object v0

    new-instance v4, Landroid/os/Bundle;

    invoke-direct {v4}, Landroid/os/Bundle;-><init>()V

    const-string v5, "version"

    invoke-virtual {v4, v5, v2}, Landroid/os/Bundle;->putShort(Ljava/lang/String;S)V

    invoke-virtual {v0, v4}, Landroid/os/Message;->setData(Landroid/os/Bundle;)V

    iget-object v2, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$2;->this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    # getter for: Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;
    invoke-static {v2}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->access$000(Lcom/giovannibozzano/wakeblock/WakeBlockService;)Landroid/os/Messenger;

    move-result-object v2

    invoke-virtual {v2, v0}, Landroid/os/Messenger;->send(Landroid/os/Message;)V
    :try_end_2b
    .catch Landroid/os/RemoteException; {:try_start_11 .. :try_end_2b} :catch_2c

    goto :goto_5a

    :catch_2c
    move-exception v0

    iget-object v2, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$2;->this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    # setter for: Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;
    invoke-static {v2, v3}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->access$002(Lcom/giovannibozzano/wakeblock/WakeBlockService;Landroid/os/Messenger;)Landroid/os/Messenger;

    iget-object v2, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$2;->this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    # setter for: Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceBound:Z
    invoke-static {v2, v1}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->access$102(Lcom/giovannibozzano/wakeblock/WakeBlockService;Z)Z

    goto :goto_5a

    :cond_38
    # getter for: Lcom/giovannibozzano/wakeblock/WakeBlockService;->lock:Ljava/lang/Object;
    invoke-static {}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->access$200()Ljava/lang/Object;

    move-result-object v0

    monitor-enter v0

    :try_start_3d
    # setter for: Lcom/giovannibozzano/wakeblock/WakeBlockService;->acquire:Z
    invoke-static {v1}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->access$302(Z)Z

    # getter for: Lcom/giovannibozzano/wakeblock/WakeBlockService;->lock:Ljava/lang/Object;
    invoke-static {}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->access$200()Ljava/lang/Object;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/Object;->notify()V

    monitor-exit v0

    goto :goto_5a

    :catchall_49
    move-exception v1

    monitor-exit v0
    :try_end_4b
    .catchall {:try_start_3d .. :try_end_4b} :catchall_49

    throw v1

    :cond_4c
    # getter for: Lcom/giovannibozzano/wakeblock/WakeBlockService;->lock:Ljava/lang/Object;
    invoke-static {}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->access$200()Ljava/lang/Object;

    move-result-object v0

    monitor-enter v0

    :try_start_51
    # getter for: Lcom/giovannibozzano/wakeblock/WakeBlockService;->lock:Ljava/lang/Object;
    invoke-static {}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->access$200()Ljava/lang/Object;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/Object;->notify()V

    monitor-exit v0

    nop

    :goto_5a
    return-void

    :catchall_5b
    move-exception v1

    monitor-exit v0
    :try_end_5d
    .catchall {:try_start_51 .. :try_end_5d} :catchall_5b

    throw v1
.end method
