.class public Lcom/giovannibozzano/wakeblock/WakeBlockService;
.super Ljava/lang/Object;
.source "WakeBlockService.java"


# static fields
.field private static final INSTANCE:Lcom/giovannibozzano/wakeblock/WakeBlockService;

.field private static final TAG:Ljava/lang/String; = "WakeBlockService"

.field private static final VERSION:S = 0x1s

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
    .registers 6

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    const/4 v0, 0x0

    iput-boolean v0, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceBound:Z

    new-instance v0, Landroid/content/Intent;

    const-string v1, "com.giovannibozzano.wakeblock.Service"

    invoke-direct {v0, v1}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    iput-object v0, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceIntent:Landroid/content/Intent;

    new-instance v0, Lcom/giovannibozzano/wakeblock/WakeBlockService$1;

    invoke-direct {v0, p0}, Lcom/giovannibozzano/wakeblock/WakeBlockService$1;-><init>(Lcom/giovannibozzano/wakeblock/WakeBlockService;)V

    iput-object v0, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceConnection:Landroid/content/ServiceConnection;

    new-instance v0, Landroid/os/HandlerThread;

    const-string v1, "wakeblock_client"

    invoke-direct {v0, v1}, Landroid/os/HandlerThread;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0}, Landroid/os/HandlerThread;->start()V

    new-instance v1, Lcom/giovannibozzano/wakeblock/WakeBlockService$2;

    invoke-virtual {v0}, Landroid/os/HandlerThread;->getLooper()Landroid/os/Looper;

    move-result-object v2

    invoke-direct {v1, p0, v2}, Lcom/giovannibozzano/wakeblock/WakeBlockService$2;-><init>(Lcom/giovannibozzano/wakeblock/WakeBlockService;Landroid/os/Looper;)V

    new-instance v2, Landroid/os/Messenger;

    invoke-direct {v2, v1}, Landroid/os/Messenger;-><init>(Landroid/os/Handler;)V

    iput-object v2, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->client:Landroid/os/Messenger;

    iget-object v2, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceIntent:Landroid/content/Intent;

    const-string v3, "system"

    const/4 v4, 0x1

    invoke-virtual {v2, v3, v4}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Z)Landroid/content/Intent;

    new-instance v2, Landroid/os/Bundle;

    invoke-direct {v2}, Landroid/os/Bundle;-><init>()V

    iget-object v3, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->client:Landroid/os/Messenger;

    invoke-virtual {v3}, Landroid/os/Messenger;->getBinder()Landroid/os/IBinder;

    move-result-object v3

    const-string v4, "messenger"

    invoke-virtual {v2, v4, v3}, Landroid/os/Bundle;->putBinder(Ljava/lang/String;Landroid/os/IBinder;)V

    iget-object v3, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceIntent:Landroid/content/Intent;

    const-string v4, "bundle"

    invoke-virtual {v3, v4, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Landroid/os/Bundle;)Landroid/content/Intent;

    iget-object v3, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceIntent:Landroid/content/Intent;

    const-string v4, "com.giovannibozzano.wakeblock"

    invoke-virtual {v3, v4}, Landroid/content/Intent;->setPackage(Ljava/lang/String;)Landroid/content/Intent;

    return-void
.end method

.method static synthetic access$000(Lcom/giovannibozzano/wakeblock/WakeBlockService;)Landroid/os/Messenger;
    .registers 2

    iget-object v0, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;

    return-object v0
.end method

.method static synthetic access$002(Lcom/giovannibozzano/wakeblock/WakeBlockService;Landroid/os/Messenger;)Landroid/os/Messenger;
    .registers 2

    iput-object p1, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;

    return-object p1
.end method

.method static synthetic access$102(Lcom/giovannibozzano/wakeblock/WakeBlockService;Z)Z
    .registers 2

    iput-boolean p1, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceBound:Z

    return p1
.end method

.method static synthetic access$200()Ljava/lang/Object;
    .registers 1

    sget-object v0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->lock:Ljava/lang/Object;

    return-object v0
.end method

.method static synthetic access$302(Z)Z
    .registers 1

    sput-boolean p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->acquire:Z

    return p0
.end method

.method static synthetic access$400(Lcom/giovannibozzano/wakeblock/WakeBlockService;)Landroid/content/Intent;
    .registers 2

    iget-object v0, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceIntent:Landroid/content/Intent;

    return-object v0
.end method

.method static synthetic access$500(Lcom/giovannibozzano/wakeblock/WakeBlockService;)Landroid/content/ServiceConnection;
    .registers 2

    iget-object v0, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceConnection:Landroid/content/ServiceConnection;

    return-object v0
.end method

.method public static getInstance()Lcom/giovannibozzano/wakeblock/WakeBlockService;
    .registers 1

    sget-object v0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->INSTANCE:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    return-object v0
.end method


# virtual methods
.method public acquireWakeLockInternal(Landroid/content/Context;Landroid/os/IBinder;Ljava/lang/String;Ljava/lang/String;)Z
    .registers 12

    const-string v0, "com.giovannibozzano.wakeblock"

    invoke-virtual {p4, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    const/4 v1, 0x0

    if-eqz v0, :cond_23

    const-string v0, "service_bind"

    invoke-virtual {p3, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_23

    iget-boolean v0, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceBound:Z

    if-nez v0, :cond_22

    new-instance v0, Ljava/lang/Thread;

    new-instance v2, Lcom/giovannibozzano/wakeblock/WakeBlockService$3;

    invoke-direct {v2, p0, p1}, Lcom/giovannibozzano/wakeblock/WakeBlockService$3;-><init>(Lcom/giovannibozzano/wakeblock/WakeBlockService;Landroid/content/Context;)V

    invoke-direct {v0, v2}, Ljava/lang/Thread;-><init>(Ljava/lang/Runnable;)V

    invoke-virtual {v0}, Ljava/lang/Thread;->start()V

    :cond_22
    return v1

    :cond_23
    iget-boolean v0, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceBound:Z

    const/4 v2, 0x1

    if-nez v0, :cond_29

    return v2

    :cond_29
    sget-object v0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->lock:Ljava/lang/Object;

    monitor-enter v0

    :try_start_2c
    sput-boolean v2, Lcom/giovannibozzano/wakeblock/WakeBlockService;->acquire:Z
    :try_end_2e
    .catchall {:try_start_2c .. :try_end_2e} :catchall_6d

    const/4 v3, 0x0

    :try_start_2f
    invoke-static {v3, v1}, Landroid/os/Message;->obtain(Landroid/os/Handler;I)Landroid/os/Message;

    move-result-object v4

    new-instance v5, Landroid/os/Bundle;

    invoke-direct {v5}, Landroid/os/Bundle;-><init>()V

    const-string v6, "lock"

    invoke-virtual {v5, v6, p2}, Landroid/os/Bundle;->putBinder(Ljava/lang/String;Landroid/os/IBinder;)V

    const-string v6, "tag"

    invoke-virtual {v5, v6, p3}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    const-string v6, "package_name"

    invoke-virtual {v5, v6, p4}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    invoke-virtual {v4, v5}, Landroid/os/Message;->setData(Landroid/os/Bundle;)V

    iget-object v6, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;

    invoke-virtual {v6, v4}, Landroid/os/Messenger;->send(Landroid/os/Message;)V
    :try_end_4f
    .catch Landroid/os/RemoteException; {:try_start_2f .. :try_end_4f} :catch_64
    .catchall {:try_start_2f .. :try_end_4f} :catchall_6d

    nop

    :try_start_50
    sget-object v1, Lcom/giovannibozzano/wakeblock/WakeBlockService;->lock:Ljava/lang/Object;

    invoke-virtual {v1}, Ljava/lang/Object;->wait()V
    :try_end_55
    .catch Ljava/lang/InterruptedException; {:try_start_50 .. :try_end_55} :catch_56
    .catchall {:try_start_50 .. :try_end_55} :catchall_6d

    goto :goto_60

    :catch_56
    move-exception v1

    :try_start_57
    const-string v2, "WakeBlockService"

    invoke-virtual {v1}, Ljava/lang/InterruptedException;->getMessage()Ljava/lang/String;

    move-result-object v3

    invoke-static {v2, v3}, Landroid/util/Slog;->e(Ljava/lang/String;Ljava/lang/String;)I

    :goto_60
    sget-boolean v1, Lcom/giovannibozzano/wakeblock/WakeBlockService;->acquire:Z

    monitor-exit v0

    return v1

    :catch_64
    move-exception v4

    iput-object v3, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;

    iput-boolean v1, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceBound:Z

    sput-boolean v2, Lcom/giovannibozzano/wakeblock/WakeBlockService;->acquire:Z

    monitor-exit v0

    return v2

    :catchall_6d
    move-exception v1

    monitor-exit v0
    :try_end_6f
    .catchall {:try_start_57 .. :try_end_6f} :catchall_6d

    throw v1
.end method

.method public removeWakeLockLocked(Landroid/os/IBinder;Ljava/lang/String;)V
    .registers 8

    iget-boolean v0, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceBound:Z

    if-nez v0, :cond_5

    return-void

    :cond_5
    sget-object v0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->lock:Ljava/lang/Object;

    monitor-enter v0

    const/4 v1, 0x1

    const/4 v2, 0x0

    :try_start_a
    invoke-static {v2, v1}, Landroid/os/Message;->obtain(Landroid/os/Handler;I)Landroid/os/Message;

    move-result-object v1

    new-instance v3, Landroid/os/Bundle;

    invoke-direct {v3}, Landroid/os/Bundle;-><init>()V

    const-string v4, "lock"

    invoke-virtual {v3, v4, p1}, Landroid/os/Bundle;->putBinder(Ljava/lang/String;Landroid/os/IBinder;)V

    const-string v4, "tag"

    invoke-virtual {v3, v4, p2}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    invoke-virtual {v1, v3}, Landroid/os/Message;->setData(Landroid/os/Bundle;)V

    iget-object v4, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;

    invoke-virtual {v4, v1}, Landroid/os/Messenger;->send(Landroid/os/Message;)V
    :try_end_25
    .catch Landroid/os/RemoteException; {:try_start_a .. :try_end_25} :catch_3a
    .catchall {:try_start_a .. :try_end_25} :catchall_38

    nop

    :try_start_26
    sget-object v1, Lcom/giovannibozzano/wakeblock/WakeBlockService;->lock:Ljava/lang/Object;

    invoke-virtual {v1}, Ljava/lang/Object;->wait()V
    :try_end_2b
    .catch Ljava/lang/InterruptedException; {:try_start_26 .. :try_end_2b} :catch_2c
    .catchall {:try_start_26 .. :try_end_2b} :catchall_38

    goto :goto_36

    :catch_2c
    move-exception v1

    :try_start_2d
    const-string v2, "WakeBlockService"

    invoke-virtual {v1}, Ljava/lang/InterruptedException;->getMessage()Ljava/lang/String;

    move-result-object v3

    invoke-static {v2, v3}, Landroid/util/Slog;->e(Ljava/lang/String;Ljava/lang/String;)I

    :goto_36
    monitor-exit v0

    return-void

    :catchall_38
    move-exception v1

    goto :goto_42

    :catch_3a
    move-exception v1

    iput-object v2, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->server:Landroid/os/Messenger;

    const/4 v2, 0x0

    iput-boolean v2, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService;->serviceBound:Z

    monitor-exit v0

    return-void

    :goto_42
    monitor-exit v0
    :try_end_43
    .catchall {:try_start_2d .. :try_end_43} :catchall_38

    throw v1
.end method
