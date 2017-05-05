.class Lcom/giovannibozzano/wakeblock/WakeBlockService$3;
.super Ljava/lang/Object;
.source "WakeBlockService.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/giovannibozzano/wakeblock/WakeBlockService;->bindService(Landroid/content/Context;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;

.field final synthetic val$context:Landroid/content/Context;


# direct methods
.method constructor <init>(Lcom/giovannibozzano/wakeblock/WakeBlockService;Landroid/content/Context;)V
    .registers 3

    iput-object p1, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$3;->this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    iput-object p2, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$3;->val$context:Landroid/content/Context;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .registers 5

    iget-object v0, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$3;->val$context:Landroid/content/Context;

    iget-object v1, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$3;->this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    invoke-static {v1}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->-get3(Lcom/giovannibozzano/wakeblock/WakeBlockService;)Landroid/content/Intent;

    move-result-object v1

    iget-object v2, p0, Lcom/giovannibozzano/wakeblock/WakeBlockService$3;->this$0:Lcom/giovannibozzano/wakeblock/WakeBlockService;

    invoke-static {v2}, Lcom/giovannibozzano/wakeblock/WakeBlockService;->-get2(Lcom/giovannibozzano/wakeblock/WakeBlockService;)Landroid/content/ServiceConnection;

    move-result-object v2

    const/4 v3, 0x1

    invoke-virtual {v0, v1, v2, v3}, Landroid/content/Context;->bindService(Landroid/content/Intent;Landroid/content/ServiceConnection;I)Z

    return-void
.end method
