import { cn } from "@/lib/utils"
import { HugeiconsIcon } from "@hugeicons/react"
import { Loading03Icon } from "@hugeicons/core-free-icons"

type SpinnerProps = Omit<React.ComponentProps<typeof HugeiconsIcon>, "icon"> & {
  icon?: React.ComponentProps<typeof HugeiconsIcon>["icon"]
}

function Spinner({
  className,
  icon: _icon,
  strokeWidth,
  ...props
}: SpinnerProps) {
  return (
    <HugeiconsIcon
      icon={Loading03Icon}
      strokeWidth={typeof strokeWidth === "number" ? strokeWidth : 2}
      role="status"
      aria-label="Loading"
      className={cn("size-4 animate-spin", className)}
      {...props}
    />
  )
}

export { Spinner }
