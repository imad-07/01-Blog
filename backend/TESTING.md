# Blog Application Testing Guide

## Overview
Comprehensive testing documentation for the Blog Application API. This covers all endpoints with both expected and unexpected behaviors.

## Test Categories

### 1. Authentication (12 tests)
- Registration: valid, duplicate username, invalid username, invalid password
- Login: valid, wrong password, non-existent user, missing credentials
- Token: expired, malformed, missing

### 2. Posts (15 tests)
- Create: text, with media, unauthorized, invalid title/content, oversized media  
- Read: feed, specific post, non-existent, user posts
- Update: own post, another's post, invalid data
- Delete: own post, another's post

### 3. Comments (8 tests)
- Create: valid, non-existent post, invalid length
- Read: post comments
- Delete: own, another's, non-existent

### 4. Likes (3 tests)
- Toggle on, toggle off, non-existent post

### 5. Follows (4 tests)
- Follow user, unfollow, self-follow, non-existent

### 6. Reports (4 tests)
- Create report, duplicate, invalid reason, non-existent post

### 7. Profiles (2 tests)
- Own profile, another's profile

### 8. Notifications (5 tests)
- Get list, mark seen, mark all seen, delete, delete all

### 9. Users (1 test)
- Paginated list

### 10. Admin Functions (10tests)
- Dashboard access, post management, report handling, user management
- Non-admin access attempts

### 11. Security (5 tests)
- XSS prevention, SQL injection, concurrent requests, rate limiting

### 12. Edge Cases (4 tests)
- Pagination overflow, special characters, stress tests, response times

**Total: 73+ test cases**

## Quick Start

```bash
cd /home/imad-07/01-Blog/backend
./test-scripts.sh
```

## Expected Error Responses

All errors follow this format:
```json
{
  "status": <number>,
  "message": "<string>"
}
```

### Common Status Codes
- `200` OK - Success
- `201` Created - Resource created
- `204` No Content - Success with no body
- `400` Bad Request - Validation error
- `401` Unauthorized - Authentication failure
- `403` Forbidden - Authorization failure
- `404` Not Found - Resource not found
- `409` Conflict - Duplicate resource
- `500` Internal Server Error - Server error

## Test Environment Requirements

1. **Backend running** on `http://localhost:8080`
2. **Database** initialized with admin user
3. **Tools**: `curl`, `jq` (for JSON parsing)
4. **Storage**: ~50MB for test media files

## Running Specific Test Categories

```bash
# Just authentication tests
grep -A 20 "## 1. Authentication Tests" test-scripts.sh | bash

# Just error cases
grep "Error:" test-scripts.sh | bash

# Performance tests only
grep -A 50 "## 12. Performance Tests" test-scripts.sh | bash
```

## Automated Test Execution

For CI/CD integration:
```bash
#!/bin/bash
# Run all tests and save results
./test-scripts.sh > test-results.log 2>&1

# Check for failures
if grep -q "FAIL" test-results.log; then
  echo "Tests failed!"
  exit 1
fi
```
