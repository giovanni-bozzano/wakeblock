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

.field private static final VERSION:S

.field private static volatile acquire:Z

.field private static volatile bindNext:Z

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

    const/4 v0, 0x0

    sput-boolean v0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->bindNext:Z

    const/4 v0, 0x1

    sput-boolean v0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->acquire:Z

    new-instance v0, Ljava/lang/Object;

    invoke-direct {v0}, Ljava/lang/Object;-><init>()V

    sput-object v0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->lock:Ljava/lang/Object;

    return-void
.end method

.method private constructor <init>()V
    .registers 7

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    const/4 v3, 0x0

    iput-boolean v3, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceBound:Z

    new-instance v3, Landroid/content/Intent;

    const-string/jumbo v4, "com.giovannibozzano.wakeblock.Service"

    invoke-direct {v3, v4}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    iput-object v3, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceIntent:Landroid/content/Intent;

    new-instance v3, Lcom/giovannibozzano/wakeblock/WakeBlockService$1;

    invoke-direct {v3, p0}, Lcom/giovannibozzano/wakeblock/WakeBlockService$1;-><init>(Lcom/giovannibozzano/wakeblock/WakeBlockService;)V

    iput-object v3, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceConnection:Landroid/content/ServiceConnection;

    new-instance v2, Landroid/os/HandlerThread;

    const-string/jumbo v3, "wakeblock_client"

    invoke-direct {v2, v3}, Landroid/os/HandlerThread;-><init>(Ljava/lang/String;)V

    invoke-virtual {v2}, Landroid/os/HandlerThread;->start()V

    new-instance v1, Lcom/giovannibozzano/wakeblock/WakeBlockService$2;

    invoke-virtual {v2}, Landroid/os/HandlerThread;->getLooper()Landroid/os/Looper;

    move-result-object v3

    invoke-direct {v1, p0, v3}, Lcom/giovannibozzano/wakeblock/WakeBlockService$2;-><init>(Lcom/giovannibozzano/wakeblock/WakeBlockService;Landroid/os/Looper;)V

    new-instance v3, Landroid/os/Messenger;

    invoke-direct {v3, v1}, Landroid/os/Messenger;-><init>(Landroid/os/Handler;)V

    iput-object v3, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->client:Landroid/os/Messenger;

    iget-object v3, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceIntent:Landroid/content/Intent;

    const-string/jumbo v4, "system"

    const/4 v5, 0x1

    invoke-virtual {v3, v4, v5}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Z)Landroid/content/Intent;

    new-instance v0, Landroid/os/Bundle;

    invoke-direct {v0}, Landroid/os/Bundle;-><init>()V

    const-string/jumbo v3, "messenger"

    iget-object v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->client:Landroid/os/Messenger;

    invoke-virtual {v4}, Landroid/os/Messenger;->getBinder()Landroid/os/IBinder;

    move-result-object v4

    invoke-virtual {v0, v3, v4}, Landroid/os/Bundle;->putBinder(Ljava/lang/String;Landroid/os/IBinder;)V

    iget-object v3, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceIntent:Landroid/content/Intent;

    const-string/jumbo v4, "bundle"

    invoke-virtual {v3, v4, v0}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Landroid/os/Bundle;)Landroid/content/Intent;

    iget-object v3, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceIntent:Landroid/content/Intent;

    const-string/jumbo v4, "com.giovannibozzano.wakeblock"

    invoke-virtual {v3, v4}, Landroid/content/Intent;->setPackage(Ljava/lang/String;)Landroid/content/Intent;

    return-void
.end method

.method public static getInstance()Lcom/giovannibozzano/wakeblock/WakeBlockService;
    .registers 1

    sget-object v0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->INSTANCE:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    return-object v0
.end method


# virtual methods
.method public bindService(Landroid/content/Context;)V
    .registers 4

    sget-boolean v0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->bindNext:Z

    if-nez v0, :cond_5

    return-void

    :cond_5
    const/4 v0, 0x0

    sput-boolean v0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->bindNext:Z

    new-instance v0, Ljava/lang/Thread;

    new-instance v1, Lcom/giovannibozzano/wakeblock/WakeBlockService$3;

    invoke-direct {v1, p0, p1}, Lcom/giovannibozzano/wakeblock/WakeBlockService$3;-><init>(Lcom/giovannibozzano/wakeblock/WakeBlockService;Landroid/content/Context;)V

    invoke-direct {v0, v1}, Ljava/lang/Thread;-><init>(Ljava/lang/Runnable;)V

    invoke-virtual {v0}, Ljava/lang/Thread;->start()V

    return-void
.end method

.method public wakeLockAcquireNew(Landroid/os/IBinder;Ljava/lang/String;Ljava/lang/String;)Z
    .registers 12

    const/4 v5, 0x0

    const/4 v7, 0x1

    const-string/jumbo v4, "com.giovannibozzano.wakeblock"

    invoke-virtual {p3, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v4

    if-eqz v4, :cond_1b

    const-string/jumbo v4, "service_bind"

    invoke-virtual {p2, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v4

    if-eqz v4, :cond_1b

    iget-boolean v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceBound:Z

    if-nez v4, :cond_1a

    sput-boolean v7, Lcom/giovannibozzano/wakeblock/WakeBlockService;->bindNext:Z

    :cond_1a
    return v5

    :cond_1b
    iget-boolean v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceBound:Z

    if-nez v4, :cond_20

    return v7

    :cond_20
    sget-object v5, Lcom/giovannibozzano/wakeblock/WakeBlockService;->lock:Ljava/lang/Object;

    monitor-enter v5

    const/4 v4, 0x1

    :try_start_24
    sput-boolean v4, Lcom/giovannibozzano/wakeblock/WakeBlockService;->acquire:Z
    :try_end_26
    .catchall {:try_start_24 .. :try_end_26} :catchall_6c

    const/4 v4, 0x0

    const/4 v6, 0x0

    :try_start_28
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

    iget-object v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;

    invoke-virtual {v4, v3}, Landroid/os/Messenger;->send(Landroid/os/Message;)V
    :try_end_4b
    .catch Landroid/os/RemoteException; {:try_start_28 .. :try_end_4b} :catch_54
    .catchall {:try_start_28 .. :try_end_4b} :catchall_6c

    :try_start_4b
    sget-object v4, Lcom/giovannibozzano/wakeblock/WakeBlockService;->lock:Ljava/lang/Object;

    invoke-virtual {v4}, Ljava/lang/Object;->wait()V
    :try_end_50
    .catch Ljava/lang/InterruptedException; {:try_start_4b .. :try_end_50} :catch_60
    .catchall {:try_start_4b .. :try_end_50} :catchall_6c

    :goto_50
    :try_start_50
    sget-boolean v4, Lcom/giovannibozzano/wakeblock/WakeBlockService;->acquire:Z
    :try_end_52
    .catchall {:try_start_50 .. :try_end_52} :catchall_6c

    monitor-exit v5

    return v4

    :catch_54
    move-exception v1

    const/4 v4, 0x0

    :try_start_56
    iput-object v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;

    const/4 v4, 0x0

    iput-boolean v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceBound:Z

    const/4 v4, 0x1

    sput-boolean v4, Lcom/giovannibozzano/wakeblock/WakeBlockService;->acquire:Z
    :try_end_5e
    .catchall {:try_start_56 .. :try_end_5e} :catchall_6c

    monitor-exit v5

    return v7

    :catch_60
    move-exception v2

    :try_start_61
    const-string/jumbo v4, "WakeBlockService"

    invoke-virtual {v2}, Ljava/lang/InterruptedException;->getMessage()Ljava/lang/String;

    move-result-object v6

    invoke-static {v4, v6}, Landroid/util/Slog;->e(Ljava/lang/String;Ljava/lang/String;)I
    :try_end_6b
    .catchall {:try_start_61 .. :try_end_6b} :catchall_6c

    goto :goto_50

    :catchall_6c
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

    iget-object v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;

    invoke-virtual {v4, v3}, Landroid/os/Messenger;->send(Landroid/os/Message;)V
    :try_end_27
    .catch Landroid/os/RemoteException; {:try_start_a .. :try_end_27} :catch_2e
    .catchall {:try_start_a .. :try_end_27} :catchall_43

    :try_start_27
    sget-object v4, Lcom/giovannibozzano/wakeblock/WakeBlockService;->lock:Ljava/lang/Object;

    invoke-virtual {v4}, Ljava/lang/Object;->wait()V
    :try_end_2c
    .catch Ljava/lang/InterruptedException; {:try_start_27 .. :try_end_2c} :catch_37
    .catchall {:try_start_27 .. :try_end_2c} :catchall_43

    :goto_2c
    monitor-exit v5

    return-void

    :catch_2e
    move-exception v1

    const/4 v4, 0x0

    :try_start_30
    iput-object v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;

    const/4 v4, 0x0

    iput-boolean v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceBound:Z
    :try_end_35
    .catchall {:try_start_30 .. :try_end_35} :catchall_43

    monitor-exit v5

    return-void

    :catch_37
    move-exception v2

    :try_start_38
    const-string/jumbo v4, "WakeBlockService"

    invoke-virtual {v2}, Ljava/lang/InterruptedException;->getMessage()Ljava/lang/String;

    move-result-object v6

    invoke-static {v4, v6}, Landroid/util/Slog;->e(Ljava/lang/String;Ljava/lang/String;)I
    :try_end_42
    .catchall {:try_start_38 .. :try_end_42} :catchall_43

    goto :goto_2c

    :catchall_43
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

    iget-object v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;

    invoke-virtual {v4, v3}, Landroid/os/Messenger;->send(Landroid/os/Message;)V
    :try_end_27
    .catch Landroid/os/RemoteException; {:try_start_a .. :try_end_27} :catch_2e
    .catchall {:try_start_a .. :try_end_27} :catchall_43

    :try_start_27
    sget-object v4, Lcom/giovannibozzano/wakeblock/WakeBlockService;->lock:Ljava/lang/Object;

    invoke-virtual {v4}, Ljava/lang/Object;->wait()V
    :try_end_2c
    .catch Ljava/lang/InterruptedException; {:try_start_27 .. :try_end_2c} :catch_37
    .catchall {:try_start_27 .. :try_end_2c} :catchall_43

    :goto_2c
    monitor-exit v5

    return-void

    :catch_2e
    move-exception v1

    const/4 v4, 0x0

    :try_start_30
    iput-object v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;

    const/4 v4, 0x0

    iput-boolean v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceBound:Z
    :try_end_35
    .catchall {:try_start_30 .. :try_end_35} :catchall_43

    monitor-exit v5

    return-void

    :catch_37
    move-exception v2

    :try_start_38
    const-string/jumbo v4, "WakeBlockService"

    invoke-virtual {v2}, Ljava/lang/InterruptedException;->getMessage()Ljava/lang/String;

    move-result-object v6

    invoke-static {v4, v6}, Landroid/util/Slog;->e(Ljava/lang/String;Ljava/lang/String;)I
    :try_end_42
    .catchall {:try_start_38 .. :try_end_42} :catchall_43

    goto :goto_2c

    :catchall_43
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

    iget-object v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;

    invoke-virtual {v4, v3}, Landroid/os/Messenger;->send(Landroid/os/Message;)V
    :try_end_33
    .catch Landroid/os/RemoteException; {:try_start_10 .. :try_end_33} :catch_3a
    .catchall {:try_start_10 .. :try_end_33} :catchall_4f

    :try_start_33
    sget-object v4, Lcom/giovannibozzano/wakeblock/WakeBlockService;->lock:Ljava/lang/Object;

    invoke-virtual {v4}, Ljava/lang/Object;->wait()V
    :try_end_38
    .catch Ljava/lang/InterruptedException; {:try_start_33 .. :try_end_38} :catch_43
    .catchall {:try_start_33 .. :try_end_38} :catchall_4f

    :goto_38
    monitor-exit v5

    return-void

    :catch_3a
    move-exception v1

    const/4 v4, 0x0

    :try_start_3c
    iput-object v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;

    const/4 v4, 0x0

    iput-boolean v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceBound:Z
    :try_end_41
    .catchall {:try_start_3c .. :try_end_41} :catchall_4f

    monitor-exit v5

    return-void

    :catch_43
    move-exception v2

    :try_start_44
    const-string/jumbo v4, "WakeBlockService"

    invoke-virtual {v2}, Ljava/lang/InterruptedException;->getMessage()Ljava/lang/String;

    move-result-object v6

    invoke-static {v4, v6}, Landroid/util/Slog;->e(Ljava/lang/String;Ljava/lang/String;)I
    :try_end_4e
    .catchall {:try_start_44 .. :try_end_4e} :catchall_4f

    goto :goto_38

    :catchall_4f
    move-exception v4

    monitor-exit v5

    throw v4
.end method
