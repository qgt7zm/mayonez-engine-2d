# Script Name: clearlogs
# Purpose:     Clears all log files and crash reports generated by the program.
# Usage:       .\clearlogs.ps1
# Author:      SlavSquatSuperstar

# Get the original calling directory
$Location = (Get-Location).Path

# Navigate to the project directory
$SCRIPT_DIR = $PSSCRIPTROOT
Set-Location "$SCRIPT_DIR"
if (-not$?)
{
    exit
}

# Delete log outputs
Remove-Item -ErrorAction SilentlyContinue -Recurse logs\
Remove-Item -ErrorAction SilentlyContinue -Recurse **\logs\
Remove-Item -ErrorAction SilentlyContinue hs_err_pid*.log
Remove-Item -ErrorAction SilentlyContinue **\hs_err_pid*.log
Write-Output "Cleared all log files."

# Exit and navigate to the previous directory
Set-Location $Location
exit 0
