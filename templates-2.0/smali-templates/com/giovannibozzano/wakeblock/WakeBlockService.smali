.class public Lcom/giovannibozzano/wakeblock/WakeBlockService;
.super Ljava/lang/Object;
.source "WakeBlockService.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/giovannibozzano/wakeblock/WakeBlockService$1;
    }
.end annotation


# static fields
.field private static final INSTANCE:Lcom/giovannibozzano/wakeblock/WakeBlockService;

.field private static final TAG:Ljava/lang/String; = "WakeBlockService"

.field private static volatile acquire:Z

.field private static bindTime:Z

.field private static final lock:Ljava/lang/Object;


# instance fields
.field private client:Landroid/os/Messenger;

.field private server:Landroid/os/Messenger;

.field private serviceBound:Z

.field private final serviceConnection:Landroid/content/ServiceConnection;

.field private final serviceIntent:Landroid/content/Intent;


# direct methods
.method static synthetic -get0()Ljava/lang/Object;
    .registers 1

    sget-object v0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->lock:Ljava/lang/Object;

    return-object v0
.end method

.method static synthetic -get1(Lcom/giovannibozzano/wakeblock/WakeBlockService;)Landroid/os/Messenger;
    .registers 2

    iget-object v0, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;

    return-object v0
.end method

.method static synthetic -get2(Lcom/giovannibozzano/wakeblock/WakeBlockService;)Landroid/content/ServiceConnection;
    .registers 2

    iget-object v0, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceConnection:Landroid/content/ServiceConnection;

    return-object v0
.end method

.method static synthetic -get3(Lcom/giovannibozzano/wakeblock/WakeBlockService;)Landroid/content/Intent;
    .registers 2

    iget-object v0, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceIntent:Landroid/content/Intent;

    return-object v0
.end method

.method static synthetic -set0(Z)Z
    .registers 1

    sput-boolean p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->acquire:Z

    return p0
.end method

.method static synthetic -set1(Lcom/giovannibozzano/wakeblock/WakeBlockService;Landroid/os/Messenger;)Landroid/os/Messenger;
    .registers 2

    iput-object p1, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;

    return-object p1
.end method

.method static synthetic -set2(Lcom/giovannibozzano/wakeblock/WakeBlockService;Z)Z
    .registers 2

    iput-boolean p1, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceBound:Z

    return p1
.end method

.method static constructor <clinit>()V
    .registers 1

    new-instance v0, Lcom/giovannibozzano/wakeblock/WakeBlockService;

    invoke-direct {v0}, Lcom/giovannibozzano/wakeblock/WakeBlockService;-><init>()V

    sput-object v0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->INSTANCE:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    const/4 v0, 0x1

    sput-boolean v0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->acquire:Z

    new-instance v0, Ljava/lang/Object;

    invoke-direct {v0}, Ljava/lang/Object;-><init>()V

    sput-object v0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->lock:Ljava/lang/Object;

    const/4 v0, 0x0

    sput-boolean v0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->bindTime:Z

    return-void
.end method

.method private constructor <init>()V
    .registers 5

    const/4 v2, 0x0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    iput-object v2, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->client:Landroid/os/Messenger;

    iput-object v2, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;

    const/4 v2, 0x0

    iput-boolean v2, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceBound:Z

    new-instance v2, Landroid/content/Intent;

    const-string/jumbo v3, "com.giovannibozzano.wakeblock.Service"

    invoke-direct {v2, v3}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    iput-object v2, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceIntent:Landroid/content/Intent;

    new-instance v2, Lcom/giovannibozzano/wakeblock/WakeBlockService$1;

    invoke-direct {v2, p0}, Lcom/giovannibozzano/wakeblock/WakeBlockService$1;-><init>(Lcom/giovannibozzano/wakeblock/WakeBlockService;)V

    iput-object v2, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceConnection:Landroid/content/ServiceConnection;

    new-instance v1, Landroid/os/HandlerThread;

    const-string/jumbo v2, "wakeblock_client"

    invoke-direct {v1, v2}, Landroid/os/HandlerThread;-><init>(Ljava/lang/String;)V

    invoke-virtual {v1}, Landroid/os/HandlerThread;->start()V

    new-instance v0, Lcom/giovannibozzano/wakeblock/WakeBlockService$2;

    invoke-virtual {v1}, Landroid/os/HandlerThread;->getLooper()Landroid/os/Looper;

    move-result-object v2

    invoke-direct {v0, p0, v2}, Lcom/giovannibozzano/wakeblock/WakeBlockService$2;-><init>(Lcom/giovannibozzano/wakeblock/WakeBlockService;Landroid/os/Looper;)V

    new-instance v2, Landroid/os/Messenger;

    invoke-direct {v2, v0}, Landroid/os/Messenger;-><init>(Landroid/os/Handler;)V

    iput-object v2, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->client:Landroid/os/Messenger;

    iget-object v2, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceIntent:Landroid/content/Intent;

    const-string/jumbo v3, "com.giovannibozzano.wakeblock"

    invoke-virtual {v2, v3}, Landroid/content/Intent;->setPackage(Ljava/lang/String;)Landroid/content/Intent;

    return-void
.end method

.method public static getInstance()Lcom/giovannibozzano/wakeblock/WakeBlockService;
    .registers 1

    sget-object v0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->INSTANCE:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    return-object v0
.end method

.method public static injectWakeBlock()V
    .registers 1

    const/4 v0, 0x1

    sput-boolean v0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->bindTime:Z

    return-void
.end method


# virtual methods
.method public bindService(Landroid/content/Context;)V
    .registers 4

    sget-boolean v0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->bindTime:Z

    if-nez v0, :cond_5

    return-void

    :cond_5
    const/4 v0, 0x0

    sput-boolean v0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->bindTime:Z

    new-instance v0, Ljava/lang/Thread;

    new-instance v1, Lcom/giovannibozzano/wakeblock/WakeBlockService$3;

    invoke-direct {v1, p0, p1}, Lcom/giovannibozzano/wakeblock/WakeBlockService$3;-><init>(Lcom/giovannibozzano/wakeblock/WakeBlockService;Landroid/content/Context;)V

    invoke-direct {v0, v1}, Ljava/lang/Thread;-><init>(Ljava/lang/Runnable;)V

    invoke-virtual {v0}, Ljava/lang/Thread;->start()V

    return-void
.end method

.method public wakeLockAcquireNew(Landroid/os/IBinder;Ljava/lang/String;Ljava/lang/String;)Z
    .registers 12

    const/4 v7, 0x1

    iget-boolean v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceBound:Z

    if-nez v4, :cond_6

    return v7

    :cond_6
    sget-object v5, Lcom/giovannibozzano/wakeblock/WakeBlockService;->lock:Ljava/lang/Object;

    monitor-enter v5

    const/4 v4, 0x1

    :try_start_a
    sput-boolean v4, Lcom/giovannibozzano/wakeblock/WakeBlockService;->acquire:Z
    :try_end_c
    .catchall {:try_start_a .. :try_end_c} :catchall_56

    const/4 v4, 0x0

    const/4 v6, 0x0

    :try_start_e
    invoke-static {v4, v6}, Landroid/os/Message;->obtain(Landroid/os/Handler;I)Landroid/os/Message;

    move-result-object v3

    new-instance v0, Landroid/os/Bundle;

    invoke-direct {v0}, Landroid/os/Bundle;-><init>()V

    const-string/jumbo v4, "lock"

    invoke-virtual {v0, v4, p1}, Landroid/os/Bundle;->putBinder(Ljava/lang/String;Landroid/os/IBinder;)V

    const-string/jumbo v4, "tag"

    invoke-virtual {v0, v4, p2}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    const-string/jumbo v4, "package_name"

    invoke-virtual {v0, v4, p3}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    invoke-virtual {v3, v0}, Landroid/os/Message;->setData(Landroid/os/Bundle;)V

    iget-object v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->client:Landroid/os/Messenger;

    iput-object v4, v3, Landroid/os/Message;->replyTo:Landroid/os/Messenger;

    iget-object v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;

    invoke-virtual {v4, v3}, Landroid/os/Messenger;->send(Landroid/os/Message;)V
    :try_end_35
    .catch Landroid/os/RemoteException; {:try_start_e .. :try_end_35} :catch_3e
    .catchall {:try_start_e .. :try_end_35} :catchall_56

    :try_start_35
    sget-object v4, Lcom/giovannibozzano/wakeblock/WakeBlockService;->lock:Ljava/lang/Object;

    invoke-virtual {v4}, Ljava/lang/Object;->wait()V
    :try_end_3a
    .catch Ljava/lang/InterruptedException; {:try_start_35 .. :try_end_3a} :catch_4a
    .catchall {:try_start_35 .. :try_end_3a} :catchall_56

    :goto_3a
    :try_start_3a
    sget-boolean v4, Lcom/giovannibozzano/wakeblock/WakeBlockService;->acquire:Z
    :try_end_3c
    .catchall {:try_start_3a .. :try_end_3c} :catchall_56

    monitor-exit v5

    return v4

    :catch_3e
    move-exception v1

    const/4 v4, 0x0

    :try_start_40
    iput-object v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;

    const/4 v4, 0x0

    iput-boolean v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceBound:Z

    const/4 v4, 0x1

    sput-boolean v4, Lcom/giovannibozzano/wakeblock/WakeBlockService;->acquire:Z
    :try_end_48
    .catchall {:try_start_40 .. :try_end_48} :catchall_56

    monitor-exit v5

    return v7

    :catch_4a
    move-exception v2

    :try_start_4b
    const-string/jumbo v4, "WakeBlockService"

    invoke-virtual {v2}, Ljava/lang/InterruptedException;->getMessage()Ljava/lang/String;

    move-result-object v6

    invoke-static {v4, v6}, Landroid/util/Slog;->e(Ljava/lang/String;Ljava/lang/String;)I
    :try_end_55
    .catchall {:try_start_4b .. :try_end_55} :catchall_56

    goto :goto_3a

    :catchall_56
    move-exception v4

    monitor-exit v5

    throw v4
.end method

.method public wakeLockHandleDeath(Landroid/os/IBinder;Ljava/lang/String;)V
    .registers 10

    iget-boolean v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceBound:Z

    if-nez v4, :cond_5

    return-void

    :cond_5
    sget-object v5, Lcom/giovannibozzano/wakeblock/WakeBlockService;->lock:Ljava/lang/Object;

    monitor-enter v5

    const/4 v4, 0x0

    const/4 v6, 0x1

    :try_start_a
    invoke-static {v4, v6}, Landroid/os/Message;->obtain(Landroid/os/Handler;I)Landroid/os/Message;

    move-result-object v3

    new-instance v0, Landroid/os/Bundle;

    invoke-direct {v0}, Landroid/os/Bundle;-><init>()V

    const-string/jumbo v4, "lock"

    invoke-virtual {v0, v4, p1}, Landroid/os/Bundle;->putBinder(Ljava/lang/String;Landroid/os/IBinder;)V

    const-string/jumbo v4, "tag"

    invoke-virtual {v0, v4, p2}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    invoke-virtual {v3, v0}, Landroid/os/Message;->setData(Landroid/os/Bundle;)V

    iget-object v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->client:Landroid/os/Messenger;

    iput-object v4, v3, Landroid/os/Message;->replyTo:Landroid/os/Messenger;

    iget-object v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;

    invoke-virtual {v4, v3}, Landroid/os/Messenger;->send(Landroid/os/Message;)V
    :try_end_2b
    .catch Landroid/os/RemoteException; {:try_start_a .. :try_end_2b} :catch_32
    .catchall {:try_start_a .. :try_end_2b} :catchall_47

    :try_start_2b
    sget-object v4, Lcom/giovannibozzano/wakeblock/WakeBlockService;->lock:Ljava/lang/Object;

    invoke-virtual {v4}, Ljava/lang/Object;->wait()V
    :try_end_30
    .catch Ljava/lang/InterruptedException; {:try_start_2b .. :try_end_30} :catch_3b
    .catchall {:try_start_2b .. :try_end_30} :catchall_47

    :goto_30
    monitor-exit v5

    return-void

    :catch_32
    move-exception v1

    const/4 v4, 0x0

    :try_start_34
    iput-object v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;

    const/4 v4, 0x0

    iput-boolean v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceBound:Z
    :try_end_39
    .catchall {:try_start_34 .. :try_end_39} :catchall_47

    monitor-exit v5

    return-void

    :catch_3b
    move-exception v2

    :try_start_3c
    const-string/jumbo v4, "WakeBlockService"

    invoke-virtual {v2}, Ljava/lang/InterruptedException;->getMessage()Ljava/lang/String;

    move-result-object v6

    invoke-static {v4, v6}, Landroid/util/Slog;->e(Ljava/lang/String;Ljava/lang/String;)I
    :try_end_46
    .catchall {:try_start_3c .. :try_end_46} :catchall_47

    goto :goto_30

    :catchall_47
    move-exception v4

    monitor-exit v5

    throw v4
.end method

.method public wakeLockRelease(Landroid/os/IBinder;Ljava/lang/String;)V
    .registers 10

    iget-boolean v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceBound:Z

    if-nez v4, :cond_5

    return-void

    :cond_5
    sget-object v5, Lcom/giovannibozzano/wakeblock/WakeBlockService;->lock:Ljava/lang/Object;

    monitor-enter v5

    const/4 v4, 0x0

    const/4 v6, 0x1

    :try_start_a
    invoke-static {v4, v6}, Landroid/os/Message;->obtain(Landroid/os/Handler;I)Landroid/os/Message;

    move-result-object v3

    new-instance v0, Landroid/os/Bundle;

    invoke-direct {v0}, Landroid/os/Bundle;-><init>()V

    const-string/jumbo v4, "lock"

    invoke-virtual {v0, v4, p1}, Landroid/os/Bundle;->putBinder(Ljava/lang/String;Landroid/os/IBinder;)V

    const-string/jumbo v4, "tag"

    invoke-virtual {v0, v4, p2}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    invoke-virtual {v3, v0}, Landroid/os/Message;->setData(Landroid/os/Bundle;)V

    iget-object v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->client:Landroid/os/Messenger;

    iput-object v4, v3, Landroid/os/Message;->replyTo:Landroid/os/Messenger;

    iget-object v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;

    invoke-virtual {v4, v3}, Landroid/os/Messenger;->send(Landroid/os/Message;)V
    :try_end_2b
    .catch Landroid/os/RemoteException; {:try_start_a .. :try_end_2b} :catch_32
    .catchall {:try_start_a .. :try_end_2b} :catchall_47

    :try_start_2b
    sget-object v4, Lcom/giovannibozzano/wakeblock/WakeBlockService;->lock:Ljava/lang/Object;

    invoke-virtual {v4}, Ljava/lang/Object;->wait()V
    :try_end_30
    .catch Ljava/lang/InterruptedException; {:try_start_2b .. :try_end_30} :catch_3b
    .catchall {:try_start_2b .. :try_end_30} :catchall_47

    :goto_30
    monitor-exit v5

    return-void

    :catch_32
    move-exception v1

    const/4 v4, 0x0

    :try_start_34
    iput-object v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;

    const/4 v4, 0x0

    iput-boolean v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceBound:Z
    :try_end_39
    .catchall {:try_start_34 .. :try_end_39} :catchall_47

    monitor-exit v5

    return-void

    :catch_3b
    move-exception v2

    :try_start_3c
    const-string/jumbo v4, "WakeBlockService"

    invoke-virtual {v2}, Ljava/lang/InterruptedException;->getMessage()Ljava/lang/String;

    move-result-object v6

    invoke-static {v4, v6}, Landroid/util/Slog;->e(Ljava/lang/String;Ljava/lang/String;)I
    :try_end_46
    .catchall {:try_start_3c .. :try_end_46} :catchall_47

    goto :goto_30

    :catchall_47
    move-exception v4

    monitor-exit v5

    throw v4
.end method

.method public wakeLockUpdateProperties(Landroid/os/IBinder;Ljava/lang/String;Ljava/lang/String;)V
    .registers 11

    iget-boolean v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceBound:Z

    if-eqz v4, :cond_a

    invoke-virtual {p2, p3}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v4

    if-eqz v4, :cond_b

    :cond_a
    return-void

    :cond_b
    sget-object v5, Lcom/giovannibozzano/wakeblock/WakeBlockService;->lock:Ljava/lang/Object;

    monitor-enter v5

    const/4 v4, 0x0

    const/4 v6, 0x2

    :try_start_10
    invoke-static {v4, v6}, Landroid/os/Message;->obtain(Landroid/os/Handler;I)Landroid/os/Message;

    move-result-object v3

    new-instance v0, Landroid/os/Bundle;

    invoke-direct {v0}, Landroid/os/Bundle;-><init>()V

    const-string/jumbo v4, "lock"

    invoke-virtual {v0, v4, p1}, Landroid/os/Bundle;->putBinder(Ljava/lang/String;Landroid/os/IBinder;)V

    const-string/jumbo v4, "old_tag"

    invoke-virtual {v0, v4, p3}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    const-string/jumbo v4, "new_tag"

    invoke-virtual {v0, v4, p3}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    invoke-virtual {v3, v0}, Landroid/os/Message;->setData(Landroid/os/Bundle;)V

    iget-object v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->client:Landroid/os/Messenger;

    iput-object v4, v3, Landroid/os/Message;->replyTo:Landroid/os/Messenger;

    iget-object v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;

    invoke-virtual {v4, v3}, Landroid/os/Messenger;->send(Landroid/os/Message;)V
    :try_end_37
    .catch Landroid/os/RemoteException; {:try_start_10 .. :try_end_37} :catch_3e
    .catchall {:try_start_10 .. :try_end_37} :catchall_53

    :try_start_37
    sget-object v4, Lcom/giovannibozzano/wakeblock/WakeBlockService;->lock:Ljava/lang/Object;

    invoke-virtual {v4}, Ljava/lang/Object;->wait()V
    :try_end_3c
    .catch Ljava/lang/InterruptedException; {:try_start_37 .. :try_end_3c} :catch_47
    .catchall {:try_start_37 .. :try_end_3c} :catchall_53

    :goto_3c
    monitor-exit v5

    return-void

    :catch_3e
    move-exception v1

    const/4 v4, 0x0

    :try_start_40
    iput-object v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;

    const/4 v4, 0x0

    iput-boolean v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceBound:Z
    :try_end_45
    .catchall {:try_start_40 .. :try_end_45} :catchall_53

    monitor-exit v5

    return-void

    :catch_47
    move-exception v2

    :try_start_48
    const-string/jumbo v4, "WakeBlockService"

    invoke-virtual {v2}, Ljava/lang/InterruptedException;->getMessage()Ljava/lang/String;

    move-result-object v6

    invoke-static {v4, v6}, Landroid/util/Slog;->e(Ljava/lang/String;Ljava/lang/String;)I
    :try_end_52
    .catchall {:try_start_48 .. :try_end_52} :catchall_53

    goto :goto_3c

    :catchall_53
    move-exception v4

    monitor-exit v5

    throw v4
.end method
