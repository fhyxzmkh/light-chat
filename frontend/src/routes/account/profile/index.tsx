import { createFileRoute, redirect } from "@tanstack/react-router";
import { useEffect } from "react";
import { useUserStore } from "@/store/UserStore.ts";
import { toast } from "sonner";

export const Route = createFileRoute("/account/profile/")({
  beforeLoad: async () => {
    const { isAuthenticated } = useUserStore.getState();
    if (!isAuthenticated) {
      toast.error("Please login to access this page.");
      throw redirect({ to: "/account/login" });
    }
  },
  component: RouteComponent,
});

function RouteComponent() {
  const userInfo = useUserStore((state) => state.user);

  useEffect(() => {
    console.log(userInfo);
  }, []);

  return (
    <>
      <div className="w-full bg-gray-200">hello</div>
    </>
  );
}
