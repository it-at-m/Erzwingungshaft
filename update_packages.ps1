# PowerShell-Skript zum Aktualisieren der Package-Deklarationen
# Von de.muenchen.eh.claim.efile nach de.muenchen.eh.integration.efile
# und von de.muenchen.eh.claim.xta nach de.muenchen.eh.integration.xta

$basePath = "C:\stef\repository\github\Erzwingungshaft\erzwingungshaft-eai\src\main\java\de\muenchen\eh\integration"

# Funktion zum Aktualisieren von Dateien
function Update-PackageDeclaration {
    param (
        [string]$filePath,
        [string]$oldPackage,
        [string]$newPackage
    )
    
    $content = Get-Content $filePath -Raw
    
    # Package-Deklaration aktualisieren
    $content = $content -replace "^package $oldPackage;`,""package $newPackage;""
    
    # Imports aktualisieren
    $content = $content -replace "import $oldPackage\.", "import $newPackage."
    
    Set-Content $filePath -Value $content
}

# Alle Java-Dateien in integration/efile verarbeiten
Get-ChildItem -Path "$basePath\efile" -Recurse -Filter "*.java" | ForEach-Object {
    Update-PackageDeclaration $_.FullName "de.muenchen.eh.claim.efile" "de.muenchen.eh.integration.efile"
    Write-Host "Aktualisiert: $($_.FullName)"
}

# Alle Java-Dateien in integration/xta verarbeiten
Get-ChildItem -Path "$basePath\xta" -Recurse -Filter "*.java" | ForEach-Object {
    Update-PackageDeclaration $_.FullName "de.muenchen.eh.claim.xta" "de.muenchen.eh.integration.xta"
    Write-Host "Aktualisiert: $($_.FullName)"
}

Write-Host "Package-Deklarationen und Imports in integration/ aktualisiert."
