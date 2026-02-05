# Git Commands Reference (Estrada Family Budget App)

## Basic Workflow
git status                  # Check what's changed
git add .                   # Stage all changes
git commit -m "Your message" # Commit with message

## Branching
git checkout -b branch-name # Create and switch to new branch
git branch                  # List branches (current has *)
git checkout branch-name    # Switch branches

## Pushing
git push origin master      # Push to master
git push -u origin branch-name # Push new branch + set upstream

## Pull & Sync
git pull origin master      # Update local from remote
git fetch                   # Fetch remote changes without merging

## Other Useful
git log                     # See commit history
git log --oneline           # Compact history
git diff                    # See unstaged changes
