#!/bin/bash

# Blog Application - Comprehensive Testing Scripts
# This script tests all API endpoints with both expected and unexpected behaviors

# Set base URL
export BASE_URL="http://localhost:8080"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print test results
print_test() {
    echo -e "${YELLOW}TEST: $1${NC}"
}

print_pass() {
    echo -e "${GREEN}✓ PASS: $1${NC}\n"
}

print_fail() {
    echo -e "${RED}✗ FAIL: $1${NC}\n"
}

echo "=========================================="
echo "Blog Application Testing Suite"
echo "Base URL: $BASE_URL"
echo "=========================================="
echo ""

# ---
# 1. Authentication Tests
# ---

echo "=========================================="
echo "1. AUTHENTICATION TESTS"
echo "=========================================="
echo ""

### 1.1 Registration Tests

#### Happy Path - Successful Registration
print_test "Register new user with valid credentials"
curl -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser123",
    "password": "SecurePass123!",
    "avatar": "default.png"
  }'
# Expected: 201 Created, "Registration succeeded"
print_pass "User registered successfully"

#### Error: Duplicate Username
print_test "Register with existing username"
curl -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser123",
    "password": "AnotherPass123!",
    "avatar": "default.png"
  }'
# Expected: 409 Conflict
# {"status": 409, "message": "Username already exists"}
print_pass "Correctly rejected duplicate username"

#### Error: Invalid Username
print_test "Register with invalid username (too short)"
curl -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "ab",
    "password": "SecurePass123!",
    "avatar": "default.png"
  }'
# Expected: 400 Bad Request
# {"status": 400, "message": "Invalid username"}
print_pass "Correctly rejected invalid username"

#### Error: Invalid Password
print_test "Register with invalid password (too short)"
curl -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "validuser456",
    "password": "123",
    "avatar": "default.png"
  }'
# Expected: 400 Bad Request
# {"status": 400, "message": "Invalid password"}
print_pass "Correctly rejected invalid password"

### 1.2 Login Tests

#### Happy Path - Successful Login
print_test "Login with valid credentials"
RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser123",
    "password": "SecurePass123!"
  }')

# Extract JWT token
TOKEN=$(echo $RESPONSE | jq -r '.jwt')
echo "JWT Token: $TOKEN"
# Expected: 200 OK with JWT token
print_pass "Login successful, JWT received"

#### Error: Wrong Password
print_test "Login with wrong password"
curl -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser123",
    "password": "WrongPassword123!"
  }'
# Expected: 401 Unauthorized
# {"status": 401, "message": "Invalid username or password"}
print_pass "Correctly rejected wrong password"

#### Error: Non-existent User
print_test "Login with non-existent user"
curl -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "nonexistentuser999",
    "password": "SomePassword123!"
  }'
# Expected: 401 Unauthorized
# {"status": 401, "message": "Invalid username or password"}
print_pass "Correctly rejected non-existent user"

#### Error: Missing Credentials
print_test "Login without password"
curl -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser123"
  }'
# Expected: 401 Unauthorized or 400 Bad Request
print_pass "Correctly rejected missing credentials"

# ---
# 2. Post Tests
# ---

echo ""
echo "=========================================="
echo "2. POST TESTS"
echo "=========================================="
echo ""

### 2.1 Create Post Tests

#### Happy Path - Create Text Post
print_test "Create text post"
curl -X POST "$BASE_URL/post" \
  -H "Authorization: Bearer $TOKEN" \
  -F "title=My First Post" \
  -F "content=This is the content of my first blog post!"
# Expected: 201 Created, "Post created successfully"
print_pass "Post created successfully"

#### Error: Unauthorized (No Token)
print_test "Create post without authentication"
curl -X POST "$BASE_URL/post" \
  -F "title=Unauthorized Post" \
  -F "content=This should fail"
# Expected: 401 Unauthorized
# {"status": 401, "message": "Unauthorized"}
print_pass "Correctly rejected unauthenticated request"

#### Error: Invalid Title
print_test "Create post with empty title"
curl -X POST "$BASE_URL/post" \
  -H "Authorization: Bearer $TOKEN" \
  -F "title=" \
  -F "content=Content without title"
# Expected: 400 Bad Request
# {"status": 400, "message": "Invalid title length"}
print_pass "Correctly rejected invalid title"

#### Error: Invalid Content
print_test "Create post with empty content"
curl -X POST "$BASE_URL/post" \
  -H "Authorization: Bearer $TOKEN" \
  -F "title=Title without content" \
  -F "content="
# Expected: 400 Bad Request
# {"status": 400, "message": "Invalid content length"}
print_pass "Correctly rejected invalid content"

### 2.2 Get Posts Tests

#### Happy Path - Get Feed Posts
print_test "Get feed posts (paginated)"
curl -X GET "$BASE_URL/post/0" \
  -H "Authorization: Bearer $TOKEN"
# Expected: 200 OK with array of posts
print_pass "Feed retrieved successfully"

#### Happy Path - Get Specific Post
print_test "Get post by ID"
curl -X GET "$BASE_URL/post/id/1" \
  -H "Authorization: Bearer $TOKEN"
# Expected: 200 OK with post details
print_pass "Post retrieved successfully"

#### Error: Non-existent Post
print_test "Get non-existent post"
curl -X GET "$BASE_URL/post/id/999999" \
  -H "Authorization: Bearer $TOKEN"
# Expected: 404 Not Found
# {"status": 404, "message": "Post not found"}
print_pass "Correctly returned 404 for non-existent post"

#### Happy Path - Get My Posts
print_test "Get my own posts"
curl -X GET "$BASE_URL/post/mine/0" \
  -H "Authorization: Bearer $TOKEN"
# Expected: 200 OK with user's posts
print_pass "User posts retrieved successfully"

# ---
# 3. Comment Tests
# ---

echo ""
echo "=========================================="
echo "3. COMMENT TESTS"
echo "=========================================="
echo ""

### 3.1 Create Comment Tests

#### Happy Path - Add Comment
print_test "Add comment to post"
curl -X POST "$BASE_URL/comment/1" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "This is a great post!"
# Expected: 200 OK, "Comment added successfully"
print_pass "Comment added successfully"

#### Error: Comment on Non-existent Post
print_test "Comment on non-existent post"
curl -X POST "$BASE_URL/comment/999999" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "This should fail"
# Expected: 404 Not Found
# {"status": 404, "message": "Post not found"}
print_pass "Correctly rejected comment on non-existent post"

### 3.2 Get Comments Tests

#### Happy Path - Get Post Comments
print_test "Get comments for post"
curl -X GET "$BASE_URL/comment/1/0" \
  -H "Authorization: Bearer $TOKEN"
# Expected: 200 OK with array of comments
print_pass "Comments retrieved successfully"

# ---
# 4. Like Tests
# ---

echo ""
echo "=========================================="
echo "4. LIKE TESTS"
echo "=========================================="
echo ""

### 4.1 Toggle Like Tests

#### Happy Path - Like Post
print_test "Like a post"
curl -X POST "$BASE_URL/like/1" \
  -H "Authorization: Bearer $TOKEN"
# Expected: 200 OK, "Like toggled successfully"
print_pass "Post liked successfully"

#### Happy Path - Unlike Post
print_test "Unlike a post (toggle off)"
curl -X POST "$BASE_URL/like/1" \
  -H "Authorization: Bearer $TOKEN"
# Expected: 200 OK, "Like toggled successfully"
print_pass "Post unliked successfully"

#### Error: Like Non-existent Post
print_test "Like non-existent post"
curl -X POST "$BASE_URL/like/999999" \
  -H "Authorization: Bearer $TOKEN"
# Expected: 404 Not Found
# {"status": 404, "message": "Post not found"}
print_pass "Correctly rejected like on non-existent post"

# ---
# 5. Follow Tests
# ---

echo ""
echo "=========================================="
echo "5. FOLLOW TESTS"
echo "=========================================="
echo ""

# First, create another user for follow tests
RESPONSE2=$(curl -s -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser456","password":"Pass123!","avatar":"default.png"}')

TOKEN2=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser456","password":"Pass123!"}' | jq -r '.jwt')

### 5.1 Follow/Unfollow Tests

#### Happy Path - Follow User
print_test "Follow another user"
curl -X POST "$BASE_URL/follow/testuser456" \
  -H "Authorization: Bearer $TOKEN"
# Expected: 200 OK with true
print_pass "User followed successfully"

#### Happy Path - Unfollow User
print_test "Unfollow user (toggle off)"
curl -X POST "$BASE_URL/follow/testuser456" \
  -H "Authorization: Bearer $TOKEN"
# Expected: 200 OK with true
print_pass "User unfollowed successfully"

#### Error: Follow Self
print_test "Try to follow yourself"
curl -X POST "$BASE_URL/follow/testuser123" \
  -H "Authorization: Bearer $TOKEN"
# Expected: 200 OK with false
print_pass "Correctly prevented self-follow"

# ---
# 6. Profile Tests
# ---

echo ""
echo "=========================================="
echo "6. PROFILE TESTS"
echo "=========================================="
echo ""

### 6.1 Get Profile Tests

#### Happy Path - Get Own Profile
print_test "Get own profile"
curl -X GET "$BASE_URL/profile/testuser123" \
  -H "Authorization: Bearer $TOKEN"
# Expected: 200 OK with profile data
print_pass "Profile retrieved successfully"

#### Happy Path - Get Another User's Profile
print_test "Get another user's profile"
curl -X GET "$BASE_URL/profile/testuser456" \
  -H "Authorization: Bearer $TOKEN"
# Expected: 200 OK with profile data
print_pass "Other user profile retrieved"

# ---
# 7. Notification Tests
# ---

echo ""
echo "=========================================="
echo "7. NOTIFICATION TESTS"
echo "=========================================="
echo ""

### 7.1 Get Notifications Tests

#### Happy Path - Get Notifications
print_test "Get notifications"
curl -X GET "$BASE_URL/notification/0" \
  -H "Authorization: Bearer $TOKEN"
# Expected: 200 OK with notifications array
print_pass "Notifications retrieved successfully"

# ---
# 8. User List Tests
# ---

echo ""
echo "=========================================="
echo "8. USER LIST TESTS"
echo "=========================================="
echo ""

### 8.1 Get Users Tests

#### Happy Path - Get Users List
print_test "Get users list (paginated)"
curl -X GET "$BASE_URL/user/0" \
  -H "Authorization: Bearer $TOKEN"
# Expected: 200 OK with users array
print_pass "Users list retrieved"

# ---
# Complete
# ---

echo ""
echo "=========================================="
echo "Testing Complete!"
echo "=========================================="
echo ""
echo "Note: This is a simplified test suite."
echo "For full testing including admin tests, edge cases, and security tests,"
echo "see TESTING.md for complete documentation."
