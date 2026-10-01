import requests
from datetime import date


BASE_URL = "http://localhost:8090"


def test_create_claim():
    session = requests.Session()

    # Get CSRF token
    csrf_response = session.get(f"{BASE_URL}/api/auth/csrf")
    assert csrf_response.status_code == 200

    csrf_data = csrf_response.json()
    csrf_token = csrf_data["token"]
    csrf_header = csrf_data["headerName"]

    # Login
    login_response = session.post(
        f"{BASE_URL}/api/auth/login",
        json={
            "email": "claimant@demo.com",
            "password": "Claimant123!"
        },
        headers={csrf_header: csrf_token}
    )

    assert login_response.status_code == 200

    # Create claim
    claim_response = session.post(
        f"{BASE_URL}/api/claims",
        json={
            "incidentDate": str(date.today()),
            "incidentLocation": "Python Test Location",
            "description": "Python API integration test",
            "claimAmount": 5000
        },
        headers={csrf_header: csrf_token}
    )

    assert claim_response.status_code == 201

    claim = claim_response.json()

    assert claim["status"] == "SUBMITTED"
    assert claim["incidentLocation"] == "Python Test Location"
    assert claim["claimAmount"] == 5000