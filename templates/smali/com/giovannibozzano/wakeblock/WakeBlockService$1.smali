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
    .registers 5

    iget-object v0, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$1;->this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    new-instance v1, Landroid/os/Messenger;

    invoke-direct {v1, p2}, Landroid/os/Messenger;-><init>(Landroid/os/IBinder;)V

    invoke-static {v0, v1}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->-set1(Lcom/giovannibozzano/wakeblock/WakeBlockService;Landroid/os/Messenger;)Landroid/os/Messenger;

    iget-object v0, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$1;->this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    const/4 v1, 0x1

    invoke-static {v0, v1}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->-set2(Lcom/giovannibozzano/wakeblock/WakeBlockService;Z)Z

    return-void
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
