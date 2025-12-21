#!/bin/bash

# Test Data Seeding Script
# This script populates the application with dummy data for testing purposes.
# Run this AFTER the application is fully started and initialized.

BASE_URL="http://localhost:4200"
API_URL="http://localhost:8080"

echo "========================================="
echo "  Blog Application - Data Seeding Script"
echo "========================================="
echo ""

# Color codes for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to create a user and return their JWT token
create_user() {
    local username=$1
    local password=$2
    local avatar=$3
    
    echo -e "${BLUE}Creating user: $username${NC}" >&2
    
    # Register the user
    local reg_response=$(curl -s -X POST "$API_URL/auth/register" \
        -H "Content-Type: application/json" \
        -d "{\"username\":\"$username\",\"password\":\"$password\",\"avatar\":\"$avatar\"}")
    
    # Check if registration failed
    if echo "$reg_response" | grep -q "error\|Error\|exception\|Exception"; then
        echo -e "${RED}Registration failed for $username: $reg_response${NC}" >&2
    fi
    
    # Login and get token
    local login_response=$(curl -s -X POST "$API_URL/auth/login" \
        -H "Content-Type: application/json" \
        -d "{\"username\":\"$username\",\"password\":\"$password\"}")
    
    local token=$(echo "$login_response" | grep -o '"jwt":"[^"]*"' | cut -d'"' -f4)
    
    if [ -z "$token" ]; then
        echo -e "${RED}Failed to get token for $username. Login response: $login_response${NC}" >&2
    fi
    
    echo "$token"
}

# Function to create a post
create_post() {
    local token=$1
    local title=$2
    local content=$3
    
    echo -e "${BLUE}Creating post: $title${NC}"
    
    curl -s -X POST "$API_URL/post" \
        -H "Authorization: Bearer $token" \
        -F "title=$title" \
        -F "content=$content" \
        
    
    sleep 0.5
}

# Function to like a post
like_post() {
    local token=$1
    local post_id=$2
    
    curl -s -X POST "$API_URL/like/$post_id" \
        -H "Authorization: Bearer $token" \
        > /dev/null
}

# Function to comment on a post
comment_on_post() {
    local token=$1
    local post_id=$2
    local text=$3
    
    curl -s -X POST "$API_URL/comment/$post_id" \
        -H "Authorization: Bearer $token" \
        -H "Content-Type: application/json" \
        -d "$text" \
        > /dev/null
}

# Function to follow a user
follow_user() {
    local token=$1
    local username=$2
    
    curl -s -X POST "$API_URL/follow/$username" \
        -H "Authorization: Bearer $token" \
        > /dev/null
}

echo "Step 1: Creating test users..."
echo "================================"

# Create test users with different avatars
TOKEN_ALICE=$(create_user "alice" "alice123" "avatar1.jpg")
TOKEN_BOB=$(create_user "bob01" "bob123" "avatar2.jpg")
TOKEN_CHARLIE=$(create_user "charlie" "charlie123" "avatar3.jpg")
TOKEN_DIANA=$(create_user "diana" "diana123" "avatar4.jpg")
TOKEN_EVE=$(create_user "eve01" "eve123" "avatar5.jpg")
TOKEN_FRANK=$(create_user "frank" "frank123" "avatar1.jpg")
TOKEN_GRACE=$(create_user "grace" "grace123" "avatar2.jpg")
TOKEN_HENRY=$(create_user "henry" "henry123" "avatar3.jpg")

echo -e "${GREEN}✓ Created 8 test users${NC}"
echo ""

echo "Step 2: Creating posts..."
echo "================================"

# Alice's posts
create_post "$TOKEN_ALICE" "Welcome to My Blog!" "This is my first post on this amazing platform. I'm excited to share my thoughts and ideas with everyone!"
create_post "$TOKEN_ALICE" "10 Tips for Productivity" "Here are my top 10 tips for staying productive: 1. Wake up early 2. Plan your day 3. Take breaks 4. Stay hydrated 5. Exercise regularly 6. Minimize distractions 7. Set clear goals 8. Learn to say no 9. Review your progress 10. Celebrate small wins!"
create_post "$TOKEN_ALICE" "My Coding Journey" "I started learning to code 6 months ago, and it's been an incredible journey. Here's what I've learned so far..."
create_post "$TOKEN_ALICE" "Remote Work Success" "After 2 years of remote work, I've learned what works and what doesn't. Here are my strategies for staying productive at home."

# Bob's posts
create_post "$TOKEN_BOB" "The Art of Coffee Making" "As a coffee enthusiast, I've spent years perfecting my brewing technique. Let me share some insights..."
create_post "$TOKEN_BOB" "Southeast Asia Adventures" "Just got back from an amazing trip! Here are my top destinations and tips for traveling in Southeast Asia."
create_post "$TOKEN_BOB" "Best Hiking Trails in Europe" "From the Alps to the Pyrenees, I've explored some incredible trails. Here are my top 10 recommendations."
create_post "$TOKEN_BOB" "Digital Nomad Life" "Living and working while traveling has been a dream come true. Let me share the realities, challenges, and rewards."

# Charlie's posts
create_post "$TOKEN_CHARLIE" "Understanding Blockchain" "Blockchain is more than just cryptocurrency. Let me break down how it works and why it matters..."
create_post "$TOKEN_CHARLIE" "The Future of AI" "Artificial Intelligence is transforming our world. Here's my take on where we're heading..."
create_post "$TOKEN_CHARLIE" "Book Review: Pragmatic Prog" "Just finished reading this classic. Here are my key takeaways for software developers..."
create_post "$TOKEN_CHARLIE" "Clean Code Principles" "Writing maintainable code is an art. Here are the principles I follow to keep my codebase clean and scalable."
create_post "$TOKEN_CHARLIE" "Docker for Beginners" "Containers revolutionized development. Here's a beginner-friendly guide to getting started with Docker."

# Diana's posts
create_post "$TOKEN_DIANA" "Healthy Meal Prep Ideas" "Meal prepping has changed my life! Here are my favorite recipes and tips for getting started."
create_post "$TOKEN_DIANA" "Yoga for Beginners" "Started practicing yoga last year. Here's what I wish I knew when I began..."
create_post "$TOKEN_DIANA" "Mindfulness in Daily Life" "Incorporating mindfulness into everyday activities has reduced my stress significantly. Here's how you can do it too."
create_post "$TOKEN_DIANA" "Plant-Based Eating Guide" "Transitioning to a plant-based diet was easier than I thought. Here's my journey and practical tips for beginners."

# Eve's posts
create_post "$TOKEN_EVE" "Photography Tips" "Want to improve your photography skills? Here are some essential tips that helped me..."
create_post "$TOKEN_EVE" "My Favorite Design Tools" "As a designer, I rely on these tools daily. Let me share my favorites and why I love them..."
create_post "$TOKEN_EVE" "Color Theory Basics" "Understanding color theory transformed my design work. Let me break down the fundamentals you need to know."
create_post "$TOKEN_EVE" "UI/UX Best Practices" "Great user experience doesn't happen by accident. Here are the principles I follow when designing interfaces."

# Frank's posts
create_post "$TOKEN_FRANK" "Startup Journey: Year One" "Launching my startup taught me more than any MBA could. Here's what the first year was really like."
create_post "$TOKEN_FRANK" "Marketing on a Budget" "You don't need millions to market effectively. Here are the strategies that worked for our bootstrapped startup."
create_post "$TOKEN_FRANK" "Managing a Remote Team" "Leading a distributed team across 5 time zones has been challenging but rewarding. Here's what I've learned."

# Grace's posts
create_post "$TOKEN_GRACE" "Data Science Career Path" "Breaking into data science can seem daunting. Here's the roadmap that worked for me and others I've mentored."
create_post "$TOKEN_GRACE" "Python for Data Analysis" "Python is the go-to language for data science. Here are the essential libraries and techniques you need to master."
create_post "$TOKEN_GRACE" "Machine Learning Basics" "ML doesn't have to be intimidating. Let me demystify the fundamentals and show you how to get started."

# Henry's posts
create_post "$TOKEN_HENRY" "Cybersecurity Essentials" "In today's digital world, security can't be an afterthought. Here are the must-know practices for everyone."
create_post "$TOKEN_HENRY" "Building Secure APIs" "API security is critical but often overlooked. Here's my checklist for building secure, production-ready APIs."
create_post "$TOKEN_HENRY" "Password Management Guide" "Using the same password everywhere? Here's why that's dangerous and how to properly manage your credentials."

echo -e "${GREEN}✓ Created 27 posts${NC}"
echo ""

echo "Step 3: Creating follow relationships..."
echo "================================"

# Create a network of follows
follow_user "$TOKEN_ALICE" "bob01"
follow_user "$TOKEN_ALICE" "charlie"
follow_user "$TOKEN_ALICE" "diana"
follow_user "$TOKEN_ALICE" "frank"

follow_user "$TOKEN_BOB" "alice"
follow_user "$TOKEN_BOB" "charlie"
follow_user "$TOKEN_BOB" "eve01"
follow_user "$TOKEN_BOB" "grace"

follow_user "$TOKEN_CHARLIE" "alice"
follow_user "$TOKEN_CHARLIE" "bob01"
follow_user "$TOKEN_CHARLIE" "diana"
follow_user "$TOKEN_CHARLIE" "eve01"
follow_user "$TOKEN_CHARLIE" "frank"
follow_user "$TOKEN_CHARLIE" "henry"

follow_user "$TOKEN_DIANA" "alice"
follow_user "$TOKEN_DIANA" "charlie"
follow_user "$TOKEN_DIANA" "grace"

follow_user "$TOKEN_EVE" "bob01"
follow_user "$TOKEN_EVE" "charlie"
follow_user "$TOKEN_EVE" "diana"
follow_user "$TOKEN_EVE" "frank"

follow_user "$TOKEN_FRANK" "alice"
follow_user "$TOKEN_FRANK" "charlie"
follow_user "$TOKEN_FRANK" "grace"
follow_user "$TOKEN_FRANK" "henry"

follow_user "$TOKEN_GRACE" "charlie"
follow_user "$TOKEN_GRACE" "diana"
follow_user "$TOKEN_GRACE" "frank"
follow_user "$TOKEN_GRACE" "henry"

follow_user "$TOKEN_HENRY" "charlie"
follow_user "$TOKEN_HENRY" "frank"
follow_user "$TOKEN_HENRY" "grace"

echo -e "${GREEN}✓ Created follow relationships${NC}"
echo ""

echo "Step 4: Adding likes to posts..."
echo "================================"

# Add likes to various posts (post IDs start from 1)
# Alice's posts get likes
like_post "$TOKEN_BOB" "1"
like_post "$TOKEN_CHARLIE" "1"
like_post "$TOKEN_DIANA" "1"
like_post "$TOKEN_EVE" "1"

like_post "$TOKEN_BOB" "2"
like_post "$TOKEN_CHARLIE" "2"
like_post "$TOKEN_DIANA" "2"

like_post "$TOKEN_CHARLIE" "3"
like_post "$TOKEN_EVE" "3"

# Bob's posts get likes
like_post "$TOKEN_ALICE" "4"
like_post "$TOKEN_CHARLIE" "4"
like_post "$TOKEN_EVE" "4"

like_post "$TOKEN_ALICE" "5"
like_post "$TOKEN_DIANA" "5"

# Charlie's posts get likes
like_post "$TOKEN_ALICE" "6"
like_post "$TOKEN_BOB" "6"
like_post "$TOKEN_DIANA" "6"
like_post "$TOKEN_EVE" "6"

like_post "$TOKEN_ALICE" "7"
like_post "$TOKEN_BOB" "7"
like_post "$TOKEN_DIANA" "7"

like_post "$TOKEN_BOB" "8"
like_post "$TOKEN_EVE" "8"

# Diana's posts get likes
like_post "$TOKEN_ALICE" "9"
like_post "$TOKEN_BOB" "9"
like_post "$TOKEN_CHARLIE" "9"

like_post "$TOKEN_CHARLIE" "10"
like_post "$TOKEN_EVE" "10"

# Eve's posts get likes
like_post "$TOKEN_ALICE" "11"
like_post "$TOKEN_BOB" "11"

like_post "$TOKEN_ALICE" "12"
like_post "$TOKEN_CHARLIE" "12"
like_post "$TOKEN_DIANA" "12"

echo -e "${GREEN}✓ Added likes to posts${NC}"
echo ""

echo "Step 5: Adding comments to posts..."
echo "================================"

# Comments on Alice's first post
comment_on_post "$TOKEN_BOB" "1" "Great first post, Alice! Looking forward to reading more from you."
comment_on_post "$TOKEN_CHARLIE" "1" "Welcome to the community! 🎉"
comment_on_post "$TOKEN_DIANA" "1" "So excited to have you here!"

# Comments on Alice's productivity post
comment_on_post "$TOKEN_BOB" "2" "These tips are gold! I especially love #7 about setting clear goals."
comment_on_post "$TOKEN_CHARLIE" "2" "I've been trying to implement these and they really work. Thanks for sharing!"
comment_on_post "$TOKEN_EVE" "2" "The tip about taking breaks is so important. Many people forget this!"

# Comments on Alice's coding journey
comment_on_post "$TOKEN_CHARLIE" "3" "Keep going! The learning never stops, but it gets easier and more fun."
comment_on_post "$TOKEN_EVE" "3" "This is inspiring! What languages are you learning?"

# Comments on Bob's coffee post
comment_on_post "$TOKEN_ALICE" "4" "As a fellow coffee lover, I appreciate this so much! What's your favorite brewing method?"
comment_on_post "$TOKEN_CHARLIE" "4" "I need to step up my coffee game. Any recommendations for beginners?"

# Comments on Bob's travel post
comment_on_post "$TOKEN_DIANA" "5" "Southeast Asia is amazing! Which country was your favorite?"
comment_on_post "$TOKEN_EVE" "5" "I've always wanted to visit Thailand. Any tips?"

# Comments on Charlie's blockchain post
comment_on_post "$TOKEN_ALICE" "6" "This really helped me understand blockchain better. Thanks for breaking it down!"
comment_on_post "$TOKEN_BOB" "6" "Great explanation! I've been curious about this for a while."
comment_on_post "$TOKEN_EVE" "6" "The examples you used made this so much clearer."

# Comments on Charlie's AI post
comment_on_post "$TOKEN_ALICE" "7" "Fascinating perspective on where AI is heading!"
comment_on_post "$TOKEN_DIANA" "7" "I'm both excited and a little nervous about the future of AI."

# Comments on Charlie's book review
comment_on_post "$TOKEN_BOB" "8" "Added to my reading list! Thanks for the recommendation."

# Comments on Diana's meal prep post
comment_on_post "$TOKEN_ALICE" "9" "I've been wanting to start meal prepping. This is so helpful!"
comment_on_post "$TOKEN_BOB" "9" "Those recipes look delicious! Going to try them this weekend."

# Comments on Diana's yoga post
comment_on_post "$TOKEN_EVE" "10" "Yoga has been life-changing for me too. What's your favorite pose?"

# Comments on Eve's photography post
comment_on_post "$TOKEN_ALICE" "11" "These tips are exactly what I needed! Thank you."
comment_on_post "$TOKEN_BOB" "11" "Great advice for beginners. The lighting tips especially."

# Comments on Eve's design tools post
comment_on_post "$TOKEN_CHARLIE" "12" "I use some of these tools too! Have you tried the new version of Figma?"
comment_on_post "$TOKEN_DIANA" "12" "As someone just getting into design, this is super helpful!"

echo -e "${GREEN}✓ Added comments to posts${NC}"
echo ""

echo "========================================="
echo -e "${GREEN}✓ Data seeding complete!${NC}"
echo "========================================="
echo ""
echo "Summary:"
echo "- 8 test users created"
echo "- 27 posts published"
echo "- 33 follow relationships established"
echo "- 80+ likes added"
echo "- 50+ comments posted"
echo ""
echo "Test credentials:"
echo "- alice / alice123"
echo "- bob01 / bob123"
echo "- charlie / charlie123"
echo "- diana / diana123"
echo "- eve01 / eve123"
echo "- frank / frank123"
echo "- grace / grace123"
echo "- henry / henry123"
echo ""
echo "Admin credentials:"
echo "- admin / admin123"
echo ""
```
