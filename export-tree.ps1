# Run it with `pwsh -NoProfile -ExecutionPolicy Bypass -File .\export-tree.ps1 -Path . -OutFile app-structure.txt -MaxDepth 6`


Param(
    [string]$Path = ".",
    [string]$OutFile = "app-structure.txt",
    [int]$MaxDepth = 10
)

$Exclude = @(".git", ".vs", "bin", "obj", "node_modules")
$sb = New-Object System.Text.StringBuilder

function Append-Line([string]$line) {
    [void]$sb.AppendLine($line)
}

function Write-Tree([string]$dir, [string]$prefix, [int]$depth) {
    if ($depth -lt 0) { return }
    $items = Get-ChildItem -LiteralPath $dir -Force -ErrorAction SilentlyContinue |
             Where-Object { $Exclude -notcontains $_.Name } |
             Sort-Object @{Expression={$_.PSIsContainer};Descending=$true}, @{Expression={$_.Name};Descending=$false}

    $count = $items.Count
    for ($i = 0; $i -lt $count; $i++) {
        $item = $items[$i]
        $isLast = ($i -eq $count - 1)
        if ($isLast) { $branch = "└─ " } else { $branch = "├─ " }

        if ($item.PSIsContainer) {
            Append-Line("$prefix$branch$($item.Name)\")
            if ($isLast) { $newPrefix = $prefix + "   " } else { $newPrefix = $prefix + "│  " }
            Write-Tree $item.FullName $newPrefix ($depth - 1)
        } else {
            Append-Line("$prefix$branch$($item.Name)")
        }
    }
}

$fullPath = Resolve-Path -LiteralPath $Path -ErrorAction Stop
Append-Line("$($fullPath.Path)\")
Write-Tree $fullPath.Path "" $MaxDepth

Set-Content -Path $OutFile -Value $sb.ToString() -Encoding UTF8
Write-Output "Wrote tree to $OutFile"