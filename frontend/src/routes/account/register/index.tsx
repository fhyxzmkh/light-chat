import { createFileRoute, useNavigate } from "@tanstack/react-router";
import { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Skeleton } from "@/components/ui/skeleton";
import { Toaster, toast } from "sonner";
import {
  MailIcon,
  UserIcon,
  LockIcon,
  RefreshCwIcon,
  ArrowRightIcon,
  KeyIcon,
} from "lucide-react";
import axios from "axios";
import { BASE_URL } from "@/config/AxiosConfig.ts";

export const Route = createFileRoute("/account/register/")({
  component: RouteComponent,
});

// 定义表单验证模式
const formSchema = z.object({
  email: z.string().email("请输入有效的邮箱地址"),
  emailCode: z.string().length(5, "邮箱验证码为5位"),
  nickname: z.string().min(2, "昵称至少2个字符").max(20, "昵称最多20个字符"),
  password: z.string().min(3, "密码至少3位").max(32, "密码最多32位"),
  checkCode: z.string().length(5, "验证码为5位"),
});

type FormValues = z.infer<typeof formSchema>;

function RouteComponent() {
  const navigate = useNavigate();

  // 状态管理
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [captchaTimestamp, setCaptchaTimestamp] = useState(Date.now());
  const [emailCaptchaTimestamp, setEmailCaptchaTimestamp] = useState(
    Date.now(),
  );
  const [captchaInput, setCaptchaInput] = useState("");
  const [emailVerifyError, setEmailVerifyError] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [captchaUrl, setCaptchaUrl] = useState("");
  const [emailCaptchaUrl, setEmailCaptchaUrl] = useState("");

  // 初始化表单
  const form = useForm<FormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      email: "",
      emailCode: "",
      nickname: "",
      password: "",
      checkCode: "",
    },
  });

  // 刷新验证码
  const refreshCaptcha = () => {
    setCaptchaTimestamp(Date.now());
    toast.info("验证码已刷新");
  };

  // 刷新邮箱验证码
  const refreshEmailCaptcha = () => {
    setEmailCaptchaTimestamp(Date.now());
  };

  // 加载验证码图片的函数
  const loadCaptchaImage = (type: number, timestamp: number) => {
    // 使用 axios 获取图片并转换为 blob URL
    return new Promise<string>((resolve) => {
      axios
        .get(`${BASE_URL}/checkCode`, {
          params: { type, t: timestamp },
          responseType: "blob",
          withCredentials: true,
        })
        .then((response) => {
          const url = URL.createObjectURL(response.data);
          resolve(url);
        })
        .catch(() => {
          // 如果失败，使用直接的URL方式
          resolve(`/checkCode?type=${type}&t=${timestamp}`);
        });
    });
  };

  // 当时间戳更新时加载验证码
  useEffect(() => {
    loadCaptchaImage(0, captchaTimestamp).then(setCaptchaUrl);
  }, [captchaTimestamp]);

  // 当邮箱验证码时间戳更新时加载验证码
  useEffect(() => {
    loadCaptchaImage(1, emailCaptchaTimestamp).then(setEmailCaptchaUrl);
  }, [emailCaptchaTimestamp]);

  // 组件挂载时初始化验证码
  useEffect(() => {
    refreshCaptcha();
  }, []);

  // 打开邮箱验证对话框
  const openEmailVerifyDialog = () => {
    setCaptchaInput("");
    setEmailVerifyError("");
    refreshEmailCaptcha();
    setIsDialogOpen(true);
  };

  // 验证图形验证码并发送邮箱验证码
  const verifyAndSendEmailCode = () => {
    const email = form.getValues("email");
    if (!email) {
      setEmailVerifyError("请先填写邮箱");
      return;
    }

    setIsLoading(true);
    // 发送邮箱验证码的请求
    axios
      .get(`${BASE_URL}/emailCode`, {
        params: {
          email,
          checkCode: captchaInput,
          type: 0,
        },
        withCredentials: true,
      })
      .then(() => {
        setIsDialogOpen(false);
        setIsLoading(false);
        toast.success("验证码已发送到您的邮箱");
      })
      .catch((error) => {
        setEmailVerifyError(error.response?.data || "发送失败，请稍后再试");
        refreshEmailCaptcha();
        setIsLoading(false);
      });
  };

  // 提交注册表单
  const onSubmit = (data: FormValues) => {
    setIsLoading(true);
    console.log(data);
    // 提交注册表单的请求
    axios
      .post(`${BASE_URL}/account/register`, data, {
        withCredentials: true,
      })
      .then(() => {
        // 注册成功，跳转到登录页
        toast.success("注册成功，即将跳转到登录页面", {
          duration: 2000,
          onAutoClose: () => {
            navigate({ to: "/account/login" });
          },
        });
      })
      .catch((error) => {
        // 处理注册失败
        form.setError("root", {
          message: error.response?.data || "注册失败，请稍后再试",
        });
        refreshCaptcha();
        setIsLoading(false);
        toast.error("注册失败: " + (error.response?.data || "请稍后再试"));
      });
  };

  return (
    <div className="flex justify-center items-center min-h-screen bg-gradient-to-b from-background to-white">
      <Toaster position="top-center" richColors />
      <div className="w-full max-w-md px-4">
        <Card className="shadow-lg border-t-4 border-primary">
          <CardHeader className="space-y-1">
            <div className="flex justify-center mb-2">
              <div className="w-16 h-16 rounded-full bg-secondary flex items-center justify-center">
                <UserIcon className="w-8 h-8 text-primary" />
              </div>
            </div>
            <CardTitle className="text-2xl font-bold text-center text-gray-800">
              创建新账号
            </CardTitle>
            <CardDescription className="text-center text-gray-500">
              填写以下信息完成注册
            </CardDescription>
          </CardHeader>
          <CardContent>
            <Form {...form}>
              <form
                onSubmit={form.handleSubmit(onSubmit)}
                className="space-y-4"
              >
                <FormField
                  control={form.control}
                  name="email"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel className="flex items-center gap-1">
                        <MailIcon className="w-4 h-4" /> 邮箱
                      </FormLabel>
                      <div className="flex gap-2">
                        <FormControl>
                          <div className="relative">
                            <Input
                              placeholder="请输入邮箱"
                              {...field}
                              className="pl-8"
                            />
                            <MailIcon className="absolute left-2.5 top-2.5 h-4 w-4 text-gray-400" />
                          </div>
                        </FormControl>
                        <Button
                          type="button"
                          variant="outline"
                          onClick={openEmailVerifyDialog}
                          disabled={isLoading}
                          className="shrink-0"
                        >
                          获取邮箱验证码
                        </Button>
                      </div>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="emailCode"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel className="flex items-center gap-1">
                        <KeyIcon className="w-4 h-4" /> 邮箱验证码
                      </FormLabel>
                      <FormControl>
                        <div className="relative">
                          <Input
                            placeholder="请输入邮箱验证码"
                            {...field}
                            className="pl-8"
                          />
                          <KeyIcon className="absolute left-2.5 top-2.5 h-4 w-4 text-gray-400" />
                        </div>
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="nickname"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel className="flex items-center gap-1">
                        <UserIcon className="w-4 h-4" /> 昵称
                      </FormLabel>
                      <FormControl>
                        <div className="relative">
                          <Input
                            placeholder="请输入昵称"
                            {...field}
                            className="pl-8"
                          />
                          <UserIcon className="absolute left-2.5 top-2.5 h-4 w-4 text-gray-400" />
                        </div>
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="password"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel className="flex items-center gap-1">
                        <LockIcon className="w-4 h-4" /> 密码
                      </FormLabel>
                      <FormControl>
                        <div className="relative">
                          <Input
                            type="password"
                            placeholder="请输入密码"
                            {...field}
                            className="pl-8"
                          />
                          <LockIcon className="absolute left-2.5 top-2.5 h-4 w-4 text-gray-400" />
                        </div>
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="checkCode"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel className="flex items-center gap-1">
                        <KeyIcon className="w-4 h-4" /> 验证码
                      </FormLabel>
                      <div className="flex gap-2">
                        <FormControl>
                          <div className="relative">
                            <Input
                              placeholder="请输入验证码"
                              {...field}
                              className="pl-8"
                            />
                            <KeyIcon className="absolute left-2.5 top-2.5 h-4 w-4 text-gray-400" />
                          </div>
                        </FormControl>
                        <div
                          className="h-9 flex items-center justify-center bg-gray-100 rounded-md cursor-pointer overflow-hidden hover:brightness-95 transition-all"
                          onClick={refreshCaptcha}
                          title="点击刷新验证码"
                        >
                          {captchaUrl ? (
                            <img
                              src={captchaUrl}
                              alt="验证码"
                              className="h-full"
                              crossOrigin="use-credentials"
                            />
                          ) : (
                            <Skeleton className="h-full w-20" />
                          )}
                        </div>
                      </div>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                {form.formState.errors.root && (
                  <div className="text-red-500 text-sm bg-red-50 p-2 rounded border border-red-200">
                    {form.formState.errors.root.message}
                  </div>
                )}

                <Button
                  type="submit"
                  className="w-full bg-primary text-primary-foreground hover:bg-primary/90"
                  disabled={isLoading}
                >
                  {isLoading ? (
                    <div className="flex items-center gap-2">
                      <div className="animate-spin">
                        <RefreshCwIcon className="h-4 w-4" />
                      </div>
                      <span>注册中...</span>
                    </div>
                  ) : (
                    <div className="flex items-center gap-2 justify-center">
                      <span>注册账号</span>
                      <ArrowRightIcon className="h-4 w-4" />
                    </div>
                  )}
                </Button>
              </form>
            </Form>
          </CardContent>
          <CardFooter className="flex justify-center border-t pt-4">
            <div className="text-sm text-gray-600">
              已有账号？
              <a
                href="/account/login"
                className="text-primary hover:underline ml-1 font-medium"
              >
                立即登录
              </a>
            </div>
          </CardFooter>
        </Card>
      </div>

      {/* 邮箱验证码验证对话框 */}
      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">
              <KeyIcon className="h-5 w-5 text-primary" />
              <span>验证身份</span>
            </DialogTitle>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label htmlFor="captchaInput" className="flex items-center gap-1">
                <KeyIcon className="w-4 h-4" /> 请输入图形验证码
              </Label>
              <div className="flex gap-2">
                <div className="relative flex-1">
                  <Input
                    id="captchaInput"
                    value={captchaInput}
                    onChange={(e) => setCaptchaInput(e.target.value)}
                    placeholder="请输入验证码"
                    className="pl-8"
                  />
                  <KeyIcon className="absolute left-2.5 top-2.5 h-4 w-4 text-gray-400" />
                </div>
                <div
                  className="h-9 flex items-center justify-center bg-gray-100 rounded-md cursor-pointer overflow-hidden hover:brightness-95 transition-all"
                  onClick={refreshEmailCaptcha}
                  title="点击刷新验证码"
                >
                  {emailCaptchaUrl ? (
                    <img
                      src={emailCaptchaUrl}
                      alt="验证码"
                      className="h-full"
                      crossOrigin="use-credentials"
                    />
                  ) : (
                    <Skeleton className="h-full w-20" />
                  )}
                </div>
              </div>
              {emailVerifyError && (
                <p className="text-red-500 text-sm bg-red-50 p-2 rounded border border-red-200">
                  {emailVerifyError}
                </p>
              )}
            </div>
          </div>
          <DialogFooter className="sm:justify-between">
            <Button variant="outline" onClick={() => setIsDialogOpen(false)}>
              取消
            </Button>
            <Button
              onClick={verifyAndSendEmailCode}
              disabled={isLoading}
              className="bg-primary text-primary-foreground hover:bg-primary/90"
            >
              {isLoading ? (
                <div className="flex items-center gap-2">
                  <div className="animate-spin">
                    <RefreshCwIcon className="h-4 w-4" />
                  </div>
                  <span>发送中...</span>
                </div>
              ) : (
                <div className="flex items-center gap-2">
                  <span>发送验证码</span>
                  <ArrowRightIcon className="h-4 w-4" />
                </div>
              )}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
