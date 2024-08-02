git add .

echo 'Enter commit message:'
read commitmessage

git commit -m "$commitmessage"
git push -u origin main

read