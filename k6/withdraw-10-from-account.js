import http from 'k6/http';

export default function () {
    const payload = JSON.stringify({amount: 10});
    const params = { headers: { 'Content-Type': 'application/json'} };
    http.post('http://localhost:8080/accounts/25a4b17c-3512-11ec-8d3d-0242ac130003/withdraw', payload, params);
}