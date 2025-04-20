import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/account/login/')({
  component: RouteComponent,
})

function RouteComponent() {
  return <div>Hello "/account/login/"!</div>
}
