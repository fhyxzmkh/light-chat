import { createRootRoute, Outlet, useMatchRoute } from "@tanstack/react-router";
import { AppSidebar } from "@/components/common/AppSidebar.tsx";
import { SidebarProvider } from "@/components/ui/sidebar.tsx";

export const Route = createRootRoute({
  component: RootComponent,
  notFoundComponent: () => {
    return <div>404!!!</div>;
  },
});

function RootComponent() {
  const hideNavRoutes = ["/account/login/", "/account/register/"];

  const matchRoute = useMatchRoute();

  const matchedHideNavRoutes = hideNavRoutes.some((route) =>
    matchRoute({ to: route }),
  );

  return (
    <>
      <div className="flex w-full">
        <div>
          {!matchedHideNavRoutes ? (
            <SidebarProvider>
              <AppSidebar />
            </SidebarProvider>
          ) : null}
        </div>
        <div className="w-full">
          <Outlet />
        </div>
      </div>
    </>
  );
}
