import http from 'k6/http';

export default function () {
  const id = "25a4b17c-3512-11ec-8d3d-0242ac130003"
  const payload = JSON.stringify({cpf: id, name: id});
  const params = { headers: { 'Content-Type': 'application/json' } };
  http.put('http://0.0.0.0:8080/accounts/25a4b17c-3512-11ec-8d3d-0242ac130003', payload, params);

  const payload2 = JSON.stringify({amount: 100});
  http.post('http://localhost:8080/accounts/25a4b17c-3512-11ec-8d3d-0242ac130003/deposit', payload2, params);
};

