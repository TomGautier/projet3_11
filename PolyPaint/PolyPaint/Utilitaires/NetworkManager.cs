using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net.Http;
using Newtonsoft.Json;

namespace PolyPaint.Utilitaires
{
    public class NetworkManager
    {
        private static readonly HttpClient client = new HttpClient();

        public NetworkManager() { }

        public async Task<string> LoginAsync(string username, string password)
        {
            var response = await client.PostAsync("http://127.0.0.1:3000/connection/login/" + username + "/" + password, null);

            return await response.Content.ReadAsStringAsync();
        }

        public async Task<string> SignupAsync(string username, string password)
        {
            var response = await client.PostAsync("http://127.0.0.1:3000/connection/signup/" + username + "/" + password, null);

            return await response.Content.ReadAsStringAsync();
        }

        internal void PostImage(string username, string sessionId, string imageId, string visibility, string protection)
        {
            var bodyTemplate = new
            {
                id = imageId,
                visibility = visibility,
                protection = protection,
                author = username
            };
            var body = new StringContent(JsonConvert.SerializeObject(bodyTemplate), Encoding.UTF8, "application/json");
            var response =  client.PostAsync("http://127.0.0.1:3000/api/images/" + sessionId + "/" + username, body);
        }
    }
}
