name "openvpn"
description "The server that runs OpenVPN"
run_list("recipe[openvpn::default]")
override_attributes(
  "openvpn" => {
    "proto" => "tcp",
    "port"  => "443",
    "key" => {
      "country" => "US",
      "province" => "DC",
      "city" => "Washington",
      "org" => "Security",
      "email" => "me@example.com"
    },
    "routes" => [
      'push "route 10.10.0.0 255.255.0.0"'
    ]
  }
)