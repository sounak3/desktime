################################################################
# Create ICO file from PNG image with inkscape and imagemagick
################################################################

# Download links
# 7-zip standalone exe
$SevenZipUtilRemote = 'https://www.7-zip.org/a/7zr.exe'
# ImageMagick portable windows zip
$ImageMagickRemote  = 'https://imagemagick.org/archive/binaries/ImageMagick-7.1.1-41-portable-Q16-x64.zip'
# Inkscape binaries bundle 7-zip archive
$InkscapeRemote     = 'https://inkscape.org/gallery/item/13317/inkscape-0.92.4-x64.7z'

# Icon image name to import
$IconImageName      = 'DeskStop-icon.png'
# Output icon file name
$IconName           = 'DeskStop.ico'

function Get-7zip {
    [CmdletBinding()]
    param (
        [Parameter(Mandatory)]
        [string] $DownloadPath
    )

    $SevenZipExe    = $($SevenZipUtilRemote.Split('/')[-1])
    Invoke-WebRequest "$SevenZipUtilRemote" -OutFile "$DownloadPath/$SevenZipExe"
    return "$DownloadPath/$SevenZipExe"
}

function Get-ImageMagick {
    [CmdletBinding()]
    param (
        [Parameter(Mandatory)]
        [string] $DownloadPath
    )

    $ImageMagickZip = $($ImageMagickRemote.Split('/')[-1])
    Invoke-WebRequest "$ImageMagickRemote" -OutFile "$DownloadPath/$ImageMagickZip"
    Expand-Archive -Path "$DownloadPath/$ImageMagickZip" -DestinationPath "$DownloadPath/" -Force
    Remove-Item "$DownloadPath/$ImageMagickZip" -Force
    $FindImgMagick = Get-ChildItem -Path $DownloadPath -Filter magick.exe -Recurse -ErrorAction SilentlyContinue -Force
    return $FindImgMagick.FullName
}

function Get-Inkscape {
    [CmdletBinding()]
    param (
        [Parameter(Mandatory)]
        [string] $DownloadPath
    )

    $SevenZipExe = Get-7zip -DownloadPath $DownloadPath
    $Inkscape7z  = $($InkscapeRemote.Split('/')[-1])
    Invoke-WebRequest "$InkscapeRemote" -OutFile "$DownloadPath/$Inkscape7z"
    Start-Process -FilePath "$SevenZipExe" -ArgumentList "x","$DownloadPath/$Inkscape7z","-o$DownloadPath","-aoa" -Wait
    Remove-Item "$DownloadPath/$Inkscape7z" -Force
    Remove-Item "$SevenZipExe" -Force
    $FindInkscape = Get-ChildItem -Path $DownloadPath -Filter inkscape.exe -Recurse -ErrorAction SilentlyContinue -Force
    return $FindInkscape.FullName
}

function Get-DeskStopPng {
    [CmdletBinding()]
    param (
        [Parameter(Mandatory)]
        [string] $DownloadPath
    )
    
    $FindDeskStopIcon = Get-ChildItem -Path "$DownloadPath\.." -Filter $IconImageName -Recurse -ErrorAction SilentlyContinue -Force
    $FindDeskStopIcon | Where-Object { $_ -notmatch 'extras' } | Copy-Item -Destination $DownloadPath -Force
    return "$DownloadPath\$IconImageName"
}

function Convert-PngToIcon {
    [CmdletBinding()]
    param (
        [Parameter(Mandatory)]
        [string] $IconFullPath,

        [Parameter(Mandatory)]
        [string] $InkscapePath,

        [Parameter(Mandatory)]
        [string] $ImageMagickPath
    )

    # Initialize an array to store the generated file names
    $files = @()
    # Define the sizes for the icons
    $sizes = @(16, 32, 48, 128, 256)
    # Output directory
    $OutDir = $IconFullPath.Split($IconImageName)[0]
    $Svg    = $OutDir + 'DeskStop.svg'

    # Import png to svg so that inkscape png import dialog box comes only once
    Start-Process -FilePath $InkscapePath -ArgumentList "-z","--export-plain-svg=$Svg","$IconFullPath" -Wait
    # Loop through each size and generate the PNG files using Inkscape
    foreach ($size in $sizes) {
        $outputFile = $OutDir + "$size.png"
        Start-Process -FilePath $InkscapePath -ArgumentList "-z","--export-png=$outputFile","--export-width=$size","--export-height=$size","$Svg" -Wait
        $files += $outputFile
    }

    # Combine the PNG files into a single ICO file using ImageMagick
    $Icon = $OutDir + $IconName
    $PngList = $files -join ' '
    Start-Process -FilePath $ImageMagickPath -ArgumentList "$PngList","-colors 256","$Icon" -Wait

    # Delete the intermediate image files
    Remove-Item $IconFullPath -Force
    Remove-Item $Svg -Force
    foreach ($file in $files) {
        Remove-Item -Force $file
    }
}

function Remove-Tools {
    [CmdletBinding()]
    param (
        [Parameter(Mandatory)]
        [string] $InkscapePath,

        [Parameter(Mandatory)]
        [string] $ImageMagickPath
    )
    
    $InkscapeParent    = $InkscapePath.Split('inkscape.exe')[0]
    $ImageMagickParent = $ImageMagickPath.Split('magick.exe')[0]

    Remove-Item $InkscapeParent -Recurse
    Remove-Item $ImageMagickParent -Recurse
}

$ErrorActionPreference = 'Stop'
$scriptpath            = $MyInvocation.MyCommand.Source
$parentdir             = Split-Path $scriptpath
$Inkscape              = Get-Inkscape -DownloadPath $parentdir
$ImageMagick           = Get-ImageMagick -DownloadPath $parentdir
$DeskStopIcon          = Get-DeskStopPng -DownloadPath $parentdir

Convert-PngToIcon -IconFullPath $DeskStopIcon -InkscapePath $Inkscape -ImageMagickPath $ImageMagick
Remove-Tools -InkscapePath $Inkscape -ImageMagickPath $ImageMagick
