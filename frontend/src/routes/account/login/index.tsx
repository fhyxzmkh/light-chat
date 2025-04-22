import { createFileRoute, useNavigate } from "@tanstack/react-router";
import { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Checkbox } from "@/components/ui/checkbox";
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
  FormDescription,
} from "@/components/ui/form";
import { Skeleton } from "@/components/ui/skeleton";
import { Toaster, toast } from "sonner";
import {
  UserIcon,
  LockIcon,
  RefreshCwIcon,
  ArrowRightIcon,
  KeyIcon,
} from "lucide-react";
import axios from "axios";
import { axiosInstance, BASE_URL } from "@/config/AxiosConfig.ts";
import { useUserStore } from "@/store/UserStore.ts";

export const Route = createFileRoute("/account/login/")({
  component: RouteComponent,
});

// 定义表单验证模式
const formSchema = z.object({
  nickname: z
    .string()
    .min(2, "用户名至少2个字符")
    .max(20, "用户名最多20个字符"),
  password: z.string().min(3, "密码至少3位").max(32, "密码最多32位"),
  checkCode: z.string().length(5, "验证码为5位"),
  rememberMe: z.boolean().default(false),
});

type FormValues = z.infer<typeof formSchema>;

// 本地存储键名
const REMEMBER_ME_KEY = "lightchat_remember_me";
const USER_CREDENTIALS_KEY = "lightchat_user_credentials";

function RouteComponent() {
  const navigate = useNavigate();

  const { setUser, setToken, setIsAuthenticated, logout } = useUserStore();

  // 状态管理
  const [captchaTimestamp, setCaptchaTimestamp] = useState(Date.now());
  const [isLoading, setIsLoading] = useState(false);
  const [captchaUrl, setCaptchaUrl] = useState("");

  // 从本地存储获取记住的凭据
  const getSavedCredentials = () => {
    const savedRememberMe = localStorage.getItem(REMEMBER_ME_KEY) === "true";
    if (savedRememberMe) {
      try {
        const savedCredentials = JSON.parse(
          localStorage.getItem(USER_CREDENTIALS_KEY) || "{}",
        );
        return {
          nickname: savedCredentials.nickname || "",
          password: savedCredentials.password || "",
          rememberMe: true,
        };
      } catch (e) {
        console.error("解析保存的凭据时出错", e);
      }
    }
    return {
      nickname: "",
      password: "",
      rememberMe: false,
    };
  };

  const savedCredentials = getSavedCredentials();

  // 初始化表单
  const form = useForm<FormValues>({
    resolver: zodResolver(formSchema) as any,
    defaultValues: {
      nickname: savedCredentials.nickname,
      password: savedCredentials.password,
      checkCode: "",
      rememberMe: savedCredentials.rememberMe,
    },
  });

  // 刷新验证码
  const refreshCaptcha = () => {
    setCaptchaTimestamp(Date.now());
    toast.info("验证码已刷新");
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

  // 组件挂载时初始化验证码
  useEffect(() => {
    refreshCaptcha();
  }, []);

  // 保存用户凭据到本地存储
  const saveUserCredentials = (data: FormValues) => {
    if (data.rememberMe) {
      localStorage.setItem(REMEMBER_ME_KEY, "true");
      localStorage.setItem(
        USER_CREDENTIALS_KEY,
        JSON.stringify({
          nickname: data.nickname,
          password: data.password,
        }),
      );
    } else {
      localStorage.removeItem(REMEMBER_ME_KEY);
      localStorage.removeItem(USER_CREDENTIALS_KEY);
    }
  };

  // 提交登录表单
  const onSubmit = (data: FormValues) => {
    setIsLoading(true);
    // 提交登录表单的请求
    axios
      .post(`${BASE_URL}/account/login`, data, {
        withCredentials: true,
      })
      .then((response) => {
        setToken(response.data.token);
        localStorage.setItem("token", response.data.token);

        // 保存用户凭据（如果选择了记住密码）
        saveUserCredentials(data);

        axiosInstance.get(`/account/profile/get`).then((res) => {
          setUser(res.data.userInfo);
        });

        setIsAuthenticated(true);
        setIsLoading(false); // 重置加载状态

        // 登录成功，跳转到首页
        toast.success("登录成功，即将跳转到首页", {
          duration: 2000,
          onAutoClose: () => {
            navigate({ to: "/account/profile" });
          },
        });
      })
      .catch((error) => {
        // 处理登录失败
        localStorage.removeItem("token");
        form.setError("root", {
          message: error.response?.data || "登录失败，请稍后再试",
        });
        refreshCaptcha();
        setIsLoading(false);
        toast.error("登录失败: " + (error.response?.data || "请稍后再试"));
      });
  };

  useEffect(() => {
    if (localStorage.getItem("token") !== null) {
      axiosInstance
        .get(`/account/profile/get`)
        .then((res) => {
          setUser(res.data.userInfo);
          setIsAuthenticated(true);
          navigate({ to: "/account/profile" });
        })
        .catch((e) => {
          console.log("自动尝试登陆失败：" + e);
          logout();
          localStorage.removeItem("token");
        });
    }
  }, []);

  return (
    <div className="flex justify-center items-center w-full min-h-screen bg-gradient-to-b from-background to-white">
      <Toaster position="top-center" richColors />
      <div className="w-full max-w-md px-4 mx-auto">
        <Card className="shadow-lg border-t-4 border-primary">
          <CardHeader className="space-y-1">
            <div className="flex justify-center mb-2">
              <div className="w-16 h-16 rounded-full bg-secondary flex items-center justify-center">
                <UserIcon className="w-8 h-8 text-primary" />
              </div>
            </div>
            <CardTitle className="text-2xl font-bold text-center text-gray-800">
              账号登录
            </CardTitle>
            <CardDescription className="text-center text-gray-500">
              欢迎回来，请登录您的账号
            </CardDescription>
          </CardHeader>
          <CardContent>
            <Form {...form}>
              <form
                onSubmit={form.handleSubmit(onSubmit as any)}
                className="space-y-4"
              >
                <FormField
                  control={form.control as any}
                  name="nickname"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel className="flex items-center gap-1">
                        <UserIcon className="w-4 h-4" /> 用户名
                      </FormLabel>
                      <FormControl>
                        <div className="relative">
                          <Input
                            placeholder="请输入用户名"
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
                  control={form.control as any}
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
                  control={form.control as any}
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

                <FormField
                  control={form.control as any}
                  name="rememberMe"
                  render={({ field }) => (
                    <FormItem className="flex flex-row items-start space-x-3 space-y-0 rounded-md p-2 hover:bg-gray-50">
                      <FormControl>
                        <Checkbox
                          checked={field.value}
                          onCheckedChange={field.onChange}
                        />
                      </FormControl>
                      <div className="space-y-1 leading-none">
                        <FormLabel className="text-sm font-medium">
                          记住密码
                        </FormLabel>
                        <FormDescription className="text-xs text-gray-500">
                          下次自动填充用户名和密码
                        </FormDescription>
                      </div>
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
                      <span>登录中...</span>
                    </div>
                  ) : (
                    <div className="flex items-center gap-2 justify-center">
                      <span>登录</span>
                      <ArrowRightIcon className="h-4 w-4" />
                    </div>
                  )}
                </Button>
              </form>
            </Form>
          </CardContent>
          <CardFooter className="flex justify-center border-t pt-4">
            <div className="text-sm text-gray-600">
              还没有账号？
              <a
                href="/account/register"
                className="text-primary hover:underline ml-1 font-medium"
              >
                立即注册
              </a>
            </div>
          </CardFooter>
        </Card>
      </div>
    </div>
  );
}
