#!/bin/sh


echo -e "[ \e[34m*\e[0m ] Please specify the location of the burp.sh [\e[36m$(pwd)\e[0m]:"

read DIR
if [[ "$DIR" == "" ]]
then
    DIR="$(pwd)"
fi
xdg-icon-resource install --size 16 burp.png application-burp
xdg-mime install x-extension-burp-mime.xml

DIR=$(echo $DIR | sed 's/\//\\\//g')
sed "s/Exec=burp/Exec=$DIR\/burp.sh/" burp.desktop > ~/.local/share/applications/burp.desktop

update-desktop-database ~/.local/share/applications
update-mime-database ~/.local/share/mime
