xdg-icon-resource install --size 16 burp.png application-burp
xdg-mime install x-extension-burp-mime.xml
cp burp.desktop ~/.local/share/applications

update-desktop-database .local/share/applications
update-mime-database .local/share/mime
