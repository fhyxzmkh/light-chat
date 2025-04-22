import {
  MessageSquare,
  Users,
  UserCircle,
  Settings,
  Search,
  UserPlus,
  Bell,
  UserRoundPlus,
  UsersRound,
} from "lucide-react";
import { Link } from "@tanstack/react-router";
import { useState } from "react";
import { cn } from "@/lib/utils";

import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarFooter,
} from "@/components/ui/sidebar";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";

// 主要菜单项
const mainMenuItems = [
  {
    title: "我的主页",
    url: "/account/profile",
    icon: UserCircle,
    badge: 0,
  },
  {
    title: "会话聊天",
    url: "/chat",
    icon: MessageSquare,
    badge: 3, // 未读消息数
  },
  {
    title: "通讯录",
    url: "/contacts",
    icon: Users,
    badge: 0,
  },
  {
    title: "设置",
    url: "/settings",
    icon: Settings,
    badge: 0,
  },
];

// 通讯录子菜单项
const contactsSubMenuItems = [
  {
    title: "联系人",
    url: "/contacts/friends",
    icon: UserCircle,
  },
  {
    title: "我创建的群聊",
    url: "/contacts/my-groups",
    icon: UsersRound,
  },
  {
    title: "我加入的群聊",
    url: "/contacts/joined-groups",
    icon: Users,
  },
];

// 操作菜单项
const actionMenuItems = [
  {
    title: "创建群聊",
    action: "createGroup",
    icon: UsersRound,
  },
  {
    title: "添加好友",
    action: "addFriend",
    icon: UserPlus,
  },
  {
    title: "添加群聊",
    action: "addGroup",
    icon: UserRoundPlus,
  },
];

export function AppSidebar() {
  const [activeMenu, setActiveMenu] = useState("会话聊天");
  const [showContactsSubMenu, setShowContactsSubMenu] = useState(false);
  // @ts-ignore
  const [newFriendRequests, setNewFriendRequests] = useState(2); // 新的好友请求数量

  // 处理菜单点击
  const handleMenuClick = (title: string) => {
    setActiveMenu(title);
    if (title === "通讯录") {
      setShowContactsSubMenu(true);
    } else {
      setShowContactsSubMenu(false);
    }
  };

  // 处理操作菜单点击
  const handleActionClick = (action: string) => {
    console.log(`Action clicked: ${action}`);
  };

  return (
    <Sidebar className="border-r h-screen flex flex-col">
      <div className="flex flex-col h-full">
        {/* 用户头像和状态 */}
        <div className="p-4 border-b">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-3">
              <Avatar className="h-10 w-10">
                <AvatarImage src="/avatars/user-avatar.png" alt="用户头像" />
                <AvatarFallback>用户</AvatarFallback>
              </Avatar>
              <div>
                <p className="text-sm font-medium">用户名</p>
                <p className="text-xs text-gray-500">在线</p>
              </div>
            </div>

            {/* 新消息通知 */}
            <TooltipProvider>
              <Tooltip>
                <TooltipTrigger asChild>
                  <div className="relative">
                    <Bell className="h-5 w-5 text-gray-500 hover:text-primary cursor-pointer" />
                    {newFriendRequests > 0 && (
                      <Badge className="absolute -top-1 -right-1 h-4 w-4 p-0 flex items-center justify-center text-[10px]">
                        {newFriendRequests}
                      </Badge>
                    )}
                  </div>
                </TooltipTrigger>
                <TooltipContent>
                  <p>新的好友请求</p>
                </TooltipContent>
              </Tooltip>
            </TooltipProvider>
          </div>
        </div>

        {/* 搜索框 */}
        <div className="px-3 py-2 border-b">
          <div className="relative">
            <Search className="absolute left-2 top-2.5 h-4 w-4 text-gray-400" />
            <input
              type="text"
              placeholder="搜索联系人/群聊"
              className="w-full pl-8 pr-4 py-2 text-sm bg-gray-100 rounded-full focus:outline-none focus:ring-1 focus:ring-primary"
            />
          </div>
        </div>

        {/* 主菜单 */}
        <SidebarContent className="flex-1 overflow-auto">
          <SidebarGroup>
            <SidebarGroupContent>
              <SidebarMenu>
                {mainMenuItems.map((item) => (
                  <SidebarMenuItem key={item.title}>
                    <SidebarMenuButton
                      asChild
                      className={cn(
                        activeMenu === item.title ? "bg-gray-100" : "",
                      )}
                      onClick={() => handleMenuClick(item.title)}
                    >
                      <Link
                        to={item.url}
                        className="flex items-center justify-between"
                      >
                        <div className="flex items-center">
                          <item.icon className="h-5 w-5 mr-3" />
                          <span>{item.title}</span>
                        </div>
                        {item.badge > 0 && (
                          <Badge className="h-5 w-5 p-0 flex items-center justify-center">
                            {item.badge}
                          </Badge>
                        )}
                      </Link>
                    </SidebarMenuButton>
                  </SidebarMenuItem>
                ))}
              </SidebarMenu>
            </SidebarGroupContent>
          </SidebarGroup>

          {/* 通讯录子菜单 */}
          {showContactsSubMenu && (
            <SidebarGroup className="mt-2">
              <SidebarGroupLabel className="text-xs text-gray-500">
                通讯录
              </SidebarGroupLabel>
              <SidebarGroupContent>
                <SidebarMenu>
                  {contactsSubMenuItems.map((item) => (
                    <SidebarMenuItem key={item.title}>
                      <SidebarMenuButton asChild>
                        <Link to={item.url} className="flex items-center pl-6">
                          <item.icon className="h-4 w-4 mr-3" />
                          <span className="text-sm">{item.title}</span>
                        </Link>
                      </SidebarMenuButton>
                    </SidebarMenuItem>
                  ))}
                </SidebarMenu>
              </SidebarGroupContent>
            </SidebarGroup>
          )}
        </SidebarContent>

        {/* 底部操作菜单 */}
        <SidebarFooter className="border-t p-2">
          <div className="flex justify-around">
            {actionMenuItems.map((item) => (
              <TooltipProvider key={item.title}>
                <Tooltip>
                  <TooltipTrigger asChild>
                    <button
                      className="p-2 rounded-full hover:bg-gray-100"
                      onClick={() => handleActionClick(item.action)}
                    >
                      <item.icon className="h-5 w-5 text-gray-600" />
                    </button>
                  </TooltipTrigger>
                  <TooltipContent>
                    <p>{item.title}</p>
                  </TooltipContent>
                </Tooltip>
              </TooltipProvider>
            ))}
          </div>
        </SidebarFooter>
      </div>
    </Sidebar>
  );
}
