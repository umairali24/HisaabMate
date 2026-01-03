
# Define sizes for standard Android launcher icons
$sizes = @{
    "mipmap-mdpi"    = 48
    "mipmap-hdpi"    = 72
    "mipmap-xhdpi"   = 96
    "mipmap-xxhdpi"  = 144
    "mipmap-xxxhdpi" = 192
}

$sourceFile = "e:\Hisaab\Logo.png"
$resDir = "e:\Hisaab\app\src\main\res"

Add-Type -AssemblyName System.Drawing

$srcImage = [System.Drawing.Image]::FromFile($sourceFile)

foreach ($mipmap in $sizes.Keys) {
    $size = $sizes[$mipmap]
    $targetDir = Join-Path $resDir $mipmap
    
    # Ensure directory exists
    if (-not (Test-Path $targetDir)) {
        New-Item -ItemType Directory -Force -Path $targetDir | Out-Null
    }

    $targetFile = Join-Path $targetDir "ic_launcher.png"
    $targetRoundFile = Join-Path $targetDir "ic_launcher_round.png"

    # Create new bitmap with target size
    $newBitmap = New-Object System.Drawing.Bitmap($size, $size)
    $graphics = [System.Drawing.Graphics]::FromImage($newBitmap)
    $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $graphics.CompositingQuality = [System.Drawing.Drawing2D.CompositingQuality]::HighQuality

    # Draw resized image
    $graphics.DrawImage($srcImage, 0, 0, $size, $size)
    
    # Save standard icon
    $newBitmap.Save($targetFile, [System.Drawing.Imaging.ImageFormat]::Png)
    
    # Save round icon (same logic for now, usually needs circle mask but simpler to use same for quick update unless we do masking)
    # Ideally should be masked, but for minimal fix, reusing same resized image is acceptable if the logo is already circular-ish or fits.
    $newBitmap.Save($targetRoundFile, [System.Drawing.Imaging.ImageFormat]::Png)

    $graphics.Dispose()
    $newBitmap.Dispose()
    
    Write-Host "Generated $mipmap ($size x $size)"
}

$srcImage.Dispose()
Write-Host "Icon generation complete."
